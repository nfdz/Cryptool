package io.github.nfdz.cryptool.shared.encryption.repository

import io.github.nfdz.cryptool.shared.core.realm.FakeRealmGateway
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.repository.realm.EncryptionRealm
import io.realm.kotlin.ext.query
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class EncryptionRepositoryTest {

    private val encryptionRealmA = EncryptionRealm().also { new ->
        new.id = "A"
        new.name = "Encryption A"
        new.password = "Password A"
        new.algorithm = AlgorithmVersion.V2.name
    }
    private val encryptionA = Encryption(
        id = "A",
        name = "Encryption A",
        password = "Password A",
        algorithm = AlgorithmVersion.V2,
        source = null,
        isFavorite = false,
        unreadMessagesCount = 0,
        lastMessage = "",
        lastMessageTimestamp = 0L,
    )
    private val encryptionB = Encryption(
        id = "B",
        name = "Encryption B",
        password = "Password B",
        algorithm = AlgorithmVersion.V1,
        source = null,
        isFavorite = false,
        unreadMessagesCount = 0,
        lastMessage = "",
        lastMessageTimestamp = 0L,
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

    @Test
    fun testGetAllEmpty() {
        val instance = EncryptionRepositoryImpl(realm)

        val result = instance.getAll()

        assertEquals(emptyList<Encryption>(), result)
    }

    @Test
    fun testGetAll() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        val result = instance.getAll()

        assertEquals(listOf(encryptionA), result)
    }

    @Test
    fun testAddAll() = runTest {
        val instance = EncryptionRepositoryImpl(realm)

        instance.addAll(listOf(encryptionA))

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        assertEquals(encryptionA, stored.first().toEntity())
    }

    @Test
    fun testObserve() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        val result = instance.observe()

        val content = result.take(1).toList().first()
        assertEquals(listOf(encryptionA), content)
    }

    @Test
    fun testObserveId() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        val result = instance.observe(id = encryptionRealmA.id)

        val content = result.take(1).toList().first()
        assertEquals(encryptionA, content)
    }

    @Test
    fun testCreate() = runTest {
        val instance = EncryptionRepositoryImpl(realm)

        instance.create(name = encryptionA.name, password = encryptionA.password, algorithm = encryptionA.algorithm)

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        val storedEncryption = stored.first().toEntity()
        assertEquals(true, storedEncryption.id != encryptionA.id)
        val storedEncryptionIgnoringId = storedEncryption.copy(id = encryptionA.id)
        assertEquals(encryptionA, storedEncryptionIgnoringId)
    }

    @Test
    fun testCreateTwice() = runTest {
        val instance = EncryptionRepositoryImpl(realm)

        instance.create(name = encryptionA.name, password = encryptionA.password, algorithm = encryptionA.algorithm)
        instance.create(name = encryptionA.name, password = encryptionA.password, algorithm = encryptionA.algorithm)

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(2, stored.size)
    }

    @Test
    fun testEdit() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        instance.edit(
            encryptionToEdit = encryptionA,
            name = encryptionB.name,
            password = encryptionB.password,
            algorithm = encryptionB.algorithm,
        )

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        val storedEncryption = stored.first().toEntity()
        assertEquals(encryptionA.id, storedEncryption.id)
        val storedEncryptionIgnoringId = storedEncryption.copy(id = encryptionB.id)
        assertEquals(encryptionB, storedEncryptionIgnoringId)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testEditNonExisting() = runTest {
        val instance = EncryptionRepositoryImpl(realm)

        instance.edit(
            encryptionToEdit = encryptionA,
            name = encryptionB.name,
            password = encryptionB.password,
            algorithm = encryptionB.algorithm,
        )
    }

    @Test
    fun testDelete() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        instance.delete(setOf(encryptionA.id))

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(0, stored.size)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testDeleteNonExisting() = runTest {
        val instance = EncryptionRepositoryImpl(realm)

        instance.delete(setOf(encryptionA.id))
    }

    @Test
    fun testSetFavorite() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        instance.setFavorite(setOf(encryptionA.id))

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        val storedEncryption = stored.first().toEntity()
        assertEquals(true, storedEncryption.isFavorite)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testSetFavoriteNonExisting() = runTest {
        val instance = EncryptionRepositoryImpl(realm)

        instance.setFavorite(setOf(encryptionA.id))
    }

    @Test
    fun testSetFavoriteTwice() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        instance.setFavorite(setOf(encryptionA.id))
        instance.setFavorite(setOf(encryptionA.id))

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        val storedEncryption = stored.first().toEntity()
        assertEquals(true, storedEncryption.isFavorite)
    }

    @Test
    fun testUnsetFavorite() = runTest {
        realm.instance.write {
            encryptionRealmA.isFavorite = true
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        instance.unsetFavorite(setOf(encryptionA.id))

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        val storedEncryption = stored.first().toEntity()
        assertEquals(false, storedEncryption.isFavorite)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testUnsetFavoriteNonExisting() = runTest {
        val instance = EncryptionRepositoryImpl(realm)

        instance.unsetFavorite(setOf(encryptionA.id))
    }

    @Test
    fun testUnsetFavoriteTwice() = runTest {
        realm.instance.write {
            encryptionRealmA.isFavorite = true
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        instance.unsetFavorite(setOf(encryptionA.id))
        instance.unsetFavorite(setOf(encryptionA.id))

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        val storedEncryption = stored.first().toEntity()
        assertEquals(false, storedEncryption.isFavorite)
    }

    @Test
    fun testSetSource() = runTest {
        realm.instance.write {
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        val source = MessageSource.MANUAL
        instance.setSource(encryptionA.id, source)

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        val storedEncryption = stored.first().toEntity()
        assertEquals(source, storedEncryption.source)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testSetSourceNonExisting() = runTest {
        val instance = EncryptionRepositoryImpl(realm)

        instance.setSource(encryptionA.id, MessageSource.MANUAL)
    }

    @Test
    fun testSetSourceNull() = runTest {
        realm.instance.write {
            encryptionRealmA.source = MessageSource.MANUAL.name
            copyToRealm(encryptionRealmA)
        }
        val instance = EncryptionRepositoryImpl(realm)

        instance.setSource(encryptionA.id, null)

        val stored = realm.instance.query<EncryptionRealm>().find()
        assertEquals(1, stored.size)
        val storedEncryption = stored.first().toEntity()
        assertEquals(null, storedEncryption.source)
    }

}