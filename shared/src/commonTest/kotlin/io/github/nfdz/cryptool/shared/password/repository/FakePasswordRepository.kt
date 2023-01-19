package io.github.nfdz.cryptool.shared.password.repository

import io.github.nfdz.cryptool.shared.password.entity.Password
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePasswordRepository(
    private val getAllAnswer: List<Password> = emptyList(),
    private val observeAnswer: Flow<List<Password>> = flow { },
    private val createAnswer: Password? = null,
    private val editAnswer: Password? = null,
) : PasswordRepository {

    var getAllCount = 0
    override fun getAll(): List<Password> {
        getAllCount++
        return getAllAnswer
    }

    var addAllRegistry = mutableListOf<List<Password>>()
    override suspend fun addAll(passwords: List<Password>) {
        addAllRegistry.add(passwords)
    }


    var observeCount = 0
    var observeArgEncryptionId: String? = null
    override suspend fun observe(): Flow<List<Password>> {
        delay(50)
        observeCount++
        return observeAnswer
    }

    var createCount = 0
    var createArgName: String? = null
    var createArgPassword: String? = null
    var createArgTags: String? = null
    override suspend fun create(name: String, password: String, tags: String): Password {
        delay(50)
        createCount++
        createArgName = name
        createArgPassword = password
        createArgTags = tags
        return createAnswer!!
    }

    var editCount = 0
    var editArgPasswordToEdit: Password? = null
    var editArgName: String? = null
    var editArgPassword: String? = null
    var editArgTags: String? = null
    override suspend fun edit(passwordToEdit: Password, name: String, password: String, tags: String): Password {
        delay(50)
        editCount++
        editArgPasswordToEdit = passwordToEdit
        editArgName = name
        editArgPassword = password
        editArgTags = tags
        return editAnswer!!
    }

    var removeCount = 0
    var removeArgPassword: Password? = null
    override suspend fun remove(password: Password) {
        removeCount++
        removeArgPassword = password
    }
}