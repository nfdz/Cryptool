package io.github.nfdz.cryptool.shared.password.repository

import io.github.nfdz.cryptool.shared.password.entity.Password
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {
    fun getAll(): List<Password>
    suspend fun addAll(passwords: List<Password>)
    suspend fun observe(): Flow<List<Password>>
    suspend fun create(name: String, password: String, tags: String): Password
    suspend fun edit(passwordToEdit: Password, name: String, password: String, tags: String): Password
    suspend fun remove(id: String)
}