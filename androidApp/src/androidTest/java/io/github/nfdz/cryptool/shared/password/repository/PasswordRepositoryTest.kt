package io.github.nfdz.cryptool.shared.password.repository

import io.github.nfdz.cryptool.shared.core.realm.FakeRealmGateway
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.repository.realm.PasswordRealm
import io.realm.kotlin.ext.query
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class PasswordRepositoryTest {

    private val passwordRealmA = PasswordRealm().also { new ->
        new.id = "A"
        new.name = "Name-A"
        new.password = "Password-A"
        new.tags = "tag-a-1, tag-a-2"
    }
    private val passwordA = Password(
        id = "A",
        name = "Name-A",
        password = "Password-A",
        tags = setOf("tag-a-1", "tag-a-2"),
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
        val instance = PasswordRepositoryImpl(realm)

        val result = instance.getAll()

        assertEquals(emptyList<Password>(), result)
    }

    @Test
    fun testGetAll() = runTest {
        realm.instance.write {
            copyToRealm(passwordRealmA)
        }
        val instance = PasswordRepositoryImpl(realm)

        val result = instance.getAll()

        assertEquals(listOf(passwordA), result)
    }

    @Test
    fun testAddAll() = runTest {
        val instance = PasswordRepositoryImpl(realm)

        instance.addAll(listOf(passwordA))

        val stored = realm.instance.query<PasswordRealm>().find()
        assertEquals(1, stored.size)
        assertEquals(passwordA, stored.first().toEntity())
    }

    @Test
    fun testObserve() = runTest {
        realm.instance.write {
            copyToRealm(passwordRealmA)
        }
        val instance = PasswordRepositoryImpl(realm)

        val result = instance.observe()

        val content = result.take(1).toList().first()
        assertEquals(listOf(passwordA), content)
    }

    @Test
    fun testCreate() = runTest {
        val instance = PasswordRepositoryImpl(realm)

        instance.create(name = passwordA.name, password = passwordA.password, tags = Password.joinTags(passwordA.tags))

        val stored = realm.instance.query<PasswordRealm>().find()
        assertEquals(1, stored.size)
        val storedPassword = stored.first().toEntity()
        assertEquals(true, storedPassword.id != passwordA.id)
        val storedPasswordIgnoringId = storedPassword.copy(id = passwordA.id)
        assertEquals(passwordA, storedPasswordIgnoringId)
    }

    @Test
    fun testCreateTwice() = runTest {
        val instance = PasswordRepositoryImpl(realm)

        instance.create(name = passwordA.name, password = passwordA.password, tags = Password.joinTags(passwordA.tags))
        instance.create(name = passwordA.name, password = passwordA.password, tags = Password.joinTags(passwordA.tags))

        val stored = realm.instance.query<PasswordRealm>().find()
        assertEquals(2, stored.size)
    }

    @Test
    fun testEdit() = runTest {
        realm.instance.write {
            copyToRealm(passwordRealmA)
        }
        val instance = PasswordRepositoryImpl(realm)

        instance.edit(
            passwordToEdit = passwordA,
            name = passwordA.name,
            password = passwordA.password,
            tags = Password.joinTags(passwordA.tags),
        )

        val stored = realm.instance.query<PasswordRealm>().find()
        assertEquals(1, stored.size)
        val storedPassword = stored.first().toEntity()
        assertEquals(passwordA.id, storedPassword.id)
        val storedPasswordIgnoringId = storedPassword.copy(id = passwordA.id)
        assertEquals(passwordA, storedPasswordIgnoringId)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testEditNonExisting() = runTest {
        val instance = PasswordRepositoryImpl(realm)

        instance.edit(
            passwordToEdit = passwordA,
            name = passwordA.name,
            password = passwordA.password,
            tags = Password.joinTags(passwordA.tags),
        )
    }

    @Test
    fun testRemove() = runTest {
        realm.instance.write {
            copyToRealm(passwordRealmA)
        }
        val instance = PasswordRepositoryImpl(realm)

        instance.remove(passwordA.id)

        val stored = realm.instance.query<PasswordRealm>().find()
        assertEquals(0, stored.size)
    }

    @Test(expected = java.util.NoSuchElementException::class)
    fun testRemoveNonExisting() = runTest {
        val instance = PasswordRepositoryImpl(realm)

        instance.remove(passwordA.id)
    }
}