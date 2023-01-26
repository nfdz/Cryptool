package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.core.realm.FakeRealmGateway
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.encryption.repository.realm.EncryptionRealm
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.repository.realm.MessageRealm
import io.github.nfdz.cryptool.shared.platform.sms.FakeSmsSender
import io.github.nfdz.cryptool.shared.platform.storage.FakeKeyValueStorage
import io.realm.kotlin.ext.query
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class MessageRepositoryTest {

    private val messageRealmA = MessageRealm().also { new ->
        new.id = "A"
        new.encryptionId = "encryptionId-A"
        new.message = "Lorem ipsum dolor A"
        new.encryptedMessage =
            "LD8azzqjc-e8C90bJ8Ut2bYa7WU.1QVQGS10pFb-LndU.128.idB9lucOGxHPLLusE_h0iumSadSum1AqzZ3fJQfCjl4OvkS-uWMSmfYb9HhAdkOeKvGP5p4vUQ"
        new.timestampInMillis = 100
        new.isFavorite = false
        new.ownership = MessageOwnership.OTHER.name
    }
    private val encryptionRealmA = EncryptionRealm().also { new ->
        new.id = "encryptionId-A"
        new.name = "Encryption A"
        new.password = "testAA"
        new.algorithm = AlgorithmVersion.V2.name
        new.source = MessageSource.Manual.serialize()
    }
    private val messageA = Message(
        id = "A",
        encryptionId = "encryptionId-A",
        message = "Lorem ipsum dolor A",
        encryptedMessage =
        "LD8azzqjc-e8C90bJ8Ut2bYa7WU.1QVQGS10pFb-LndU.128.idB9lucOGxHPLLusE_h0iumSadSum1AqzZ3fJQfCjl4OvkS-uWMSmfYb9HhAdkOeKvGP5p4vUQ",
        timestampInMillis = 100,
        isFavorite = false,
        ownership = MessageOwnership.OTHER,
    )

    private lateinit var realm: FakeRealmGateway
    private val keyValueStorage = FakeKeyValueStorage()

    @Before
    fun beforeTest() {
        realm = FakeRealmGateway()
    }

    @After
    fun afterTest() {
        realm.tearDownTest()
    }

    @Test
    fun testGetAllEmpty() {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        val result = instance.getAll()

        assertEquals(emptyList<Message>(), result)
    }

    @Test
    fun testGetAll() = runTest {
        realm.instance.write {
            copyToRealm(messageRealmA)
        }
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        val result = instance.getAll()

        assertEquals(listOf(messageA), result)
    }

    @Test
    fun testAddAll() = runTest {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.addAll(listOf(messageA))

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        assertEquals(messageA, stored.first().toEntity())
    }

    @Test
    fun testObserve() = runTest {
        realm.instance.write {
            copyToRealm(messageRealmA)
        }
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        val result = instance.observe(encryptionId = messageRealmA.encryptionId)

        val content = result.take(1).toList().first()
        assertEquals(listOf(messageA), content)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testReceiveMessageWithInvalidEncryption() = runTest {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.receiveMessage(encryptionId = "Invalid", encryptedMessage = messageA.encryptedMessage)
    }

    @Test(expected = java.lang.IllegalStateException::class)
    fun testReceiveMessageWithInvalidMessage() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }

        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.receiveMessage(encryptionId = messageA.encryptionId, encryptedMessage = "Invalid")
    }

    @Test
    fun testReceiveMessage() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.receiveMessage(encryptionId = messageA.encryptionId, encryptedMessage = messageA.encryptedMessage)

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(MessageOwnership.OTHER, storedMessage.ownership)
        assertEquals(messageA.message, storedMessage.message)
        assertEquals(messageA.encryptedMessage, storedMessage.encryptedMessage)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testSendMessageWithInvalidEncryption() = runTest {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.sendMessage(encryptionId = "Invalid", message = messageA.message)
    }

    @Test(expected = java.lang.IllegalArgumentException::class)
    fun testSendMessageWithSmsError() = runTest {
        encryptionRealmA.source = MessageSource.Sms("123").serialize()
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }

        val instance = MessageRepositoryImpl(
            realm,
            keyValueStorage,
            FakeSmsSender(sendMessageException = java.lang.IllegalArgumentException())
        )

        instance.sendMessage(encryptionId = messageA.encryptionId, message = messageA.message)
    }

    @Test
    fun testSendMessage() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val smsSender = FakeSmsSender()
        val instance = MessageRepositoryImpl(realm, keyValueStorage, smsSender)

        instance.sendMessage(encryptionId = messageA.encryptionId, message = messageA.message)

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(messageA.message, storedMessage.message)
        assertEquals(MessageOwnership.OWN, storedMessage.ownership)
        assertEquals(0, smsSender.sendMessageCount)
    }

    @Test
    fun testSendMessageSms() = runTest {
        val phone = "1234"
        encryptionRealmA.source = MessageSource.Sms(phone).serialize()
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val smsSender = FakeSmsSender()
        val instance = MessageRepositoryImpl(realm, keyValueStorage, smsSender)

        instance.sendMessage(encryptionId = messageA.encryptionId, message = messageA.message)

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(messageA.message, storedMessage.message)
        assertEquals(MessageOwnership.OWN, storedMessage.ownership)
        assertEquals(1, smsSender.sendMessageCount)
        assertEquals(phone, smsSender.sendMessageArgPhone)
    }

    @Test
    fun testDelete() = runTest {
        realm.instance.write {
            copyToRealm(messageRealmA)
        }
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.delete(setOf(messageA.id))

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(0, stored.size)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testDeleteNonExisting() = runTest {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.delete(setOf(messageA.id))
    }

    @Test
    fun testSetFavorite() = runTest {
        realm.instance.write {
            copyToRealm(messageRealmA)
        }
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.setFavorite(setOf(messageA.id))

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(true, storedMessage.isFavorite)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testSetFavoriteNonExisting() = runTest {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.setFavorite(setOf(messageA.id))
    }

    @Test
    fun testSetFavoriteTwice() = runTest {
        realm.instance.write {
            copyToRealm(messageRealmA)
        }
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.setFavorite(setOf(messageA.id))
        instance.setFavorite(setOf(messageA.id))

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(true, storedMessage.isFavorite)
    }

    @Test
    fun testUnsetFavorite() = runTest {
        realm.instance.write {
            messageRealmA.isFavorite = true
            copyToRealm(messageRealmA)
        }
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.unsetFavorite(setOf(messageA.id))

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(false, storedMessage.isFavorite)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testUnsetFavoriteNonExisting() = runTest {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.unsetFavorite(setOf(messageA.id))
    }

    @Test
    fun testUnsetFavoriteTwice() = runTest {
        realm.instance.write {
            messageRealmA.isFavorite = true
            copyToRealm(messageRealmA)
        }
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        instance.unsetFavorite(setOf(messageA.id))
        instance.unsetFavorite(setOf(messageA.id))

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(false, storedMessage.isFavorite)
    }

    @Test
    fun testGetVisibilityPreferenceDefault() = runTest {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        val result = instance.getVisibilityPreference()

        assertEquals(MessageRepositoryImpl.defaultVisibility, result)
    }

    @Test
    fun testGetVisibilityPreference() = runTest {
        val visibility = false
        keyValueStorage.map[MessageRepositoryImpl.visibilityKey] = visibility
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        val result = instance.getVisibilityPreference()

        assertEquals(visibility, result)
    }

    @Test
    fun testSetVisibilityPreference() = runTest {
        val instance = MessageRepositoryImpl(realm, keyValueStorage, FakeSmsSender())

        val visibility = false
        instance.setVisibilityPreference(visibility)

        val storedVisibility = keyValueStorage.map[MessageRepositoryImpl.visibilityKey]
        assertEquals(visibility, storedVisibility)
    }
}