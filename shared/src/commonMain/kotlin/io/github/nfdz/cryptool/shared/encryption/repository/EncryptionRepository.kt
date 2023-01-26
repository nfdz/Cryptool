package io.github.nfdz.cryptool.shared.encryption.repository

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import kotlinx.coroutines.flow.Flow

interface EncryptionRepository {

    fun getAll(): List<Encryption>
    fun getAllWith(source: MessageSource): List<Encryption>
    suspend fun addAll(encryptions: List<Encryption>)
    suspend fun observe(): Flow<List<Encryption>>
    suspend fun observe(id: String): Flow<Encryption>
    suspend fun create(name: String, password: String, algorithm: AlgorithmVersion): Encryption
    suspend fun edit(
        encryptionToEdit: Encryption,
        name: String,
        password: String,
        algorithm: AlgorithmVersion
    ): Encryption

    suspend fun delete(ids: Set<String>)
    suspend fun setFavorite(ids: Set<String>)
    suspend fun unsetFavorite(ids: Set<String>)
    suspend fun setSource(id: String, source: MessageSource?)
    suspend fun acknowledgeUnreadMessages(id: String)
}