package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.core.realm.FakeRealmGateway
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.encryption.repository.realm.EncryptionRealm
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.repository.realm.MessageRealm
import io.realm.kotlin.ext.query
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class MessageReceiverTest {

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

    @Before
    fun beforeTest() {
        realm = FakeRealmGateway()
    }

    @After
    fun afterTest() {
        realm.tearDownTest()
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testReceiveWithInvalidEncryption() = runTest {
        val instance = MessageReceiverImpl(realm)

        instance.receive(encryptionId = "Invalid", encryptedMessage = messageA.encryptedMessage, isRead = false)
    }

    @Test(expected = java.lang.IllegalStateException::class)
    fun testReceiveWithInvalidMessage() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = MessageReceiverImpl(realm)

        instance.receive(encryptionId = messageA.encryptionId, encryptedMessage = "Invalid", isRead = false)
    }

    @Test
    fun testReceive() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = MessageReceiverImpl(realm)

        instance.receive(
            encryptionId = messageA.encryptionId,
            encryptedMessage = messageA.encryptedMessage,
            isRead = false,
        )

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(MessageOwnership.OTHER, storedMessage.ownership)
        assertEquals(messageA.message, storedMessage.message)
        assertEquals(messageA.encryptedMessage, storedMessage.encryptedMessage)
        val storedEncryptions = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, storedEncryptions.size)
        val storedEncryption = storedEncryptions.first().toEntity()
        assertEquals(1, storedEncryption.unreadMessagesCount)
        assertEquals("${encryptionRealmA.name}: ${messageA.encryptedMessage}", storedEncryption.lastMessage)
    }

    @Test
    fun testReceive2() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = MessageReceiverImpl(realm)

        instance.receive(
            encryption = encryptionRealmA.toEntity(),
            encryptedMessage = messageA.encryptedMessage,
            timestampInMillis = messageA.timestampInMillis,
            isRead = false,
        )

        val stored = realm.instance.query<MessageRealm>().find()
        assertEquals(1, stored.size)
        val storedMessage = stored.first().toEntity()
        assertEquals(MessageOwnership.OTHER, storedMessage.ownership)
        assertEquals(messageA.message, storedMessage.message)
        assertEquals(messageA.encryptedMessage, storedMessage.encryptedMessage)
        val storedEncryptions = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, storedEncryptions.size)
        val storedEncryption = storedEncryptions.first().toEntity()
        assertEquals(1, storedEncryption.unreadMessagesCount)
        assertEquals("${encryptionRealmA.name}: ${messageA.encryptedMessage}", storedEncryption.lastMessage)
        assertEquals(messageA.timestampInMillis, storedEncryption.lastMessageTimestamp)
    }
}