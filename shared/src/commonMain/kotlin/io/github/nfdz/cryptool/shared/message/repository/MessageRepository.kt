package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.message.entity.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getAll(): List<Message>
    suspend fun addAll(messages: List<Message>)
    suspend fun observe(encryptionId: String): Flow<List<Message>>
    suspend fun receiveMessage(encryptionId: String, encryptedMessage: String)
    suspend fun receiveMessageAsync(
        encryptionId: String,
        message: String,
        encryptedMessage: String,
        timestampInMillis: Long,
    )

    suspend fun sendMessage(encryptionId: String, message: String)
    suspend fun delete(messageIds: Set<String>)
    suspend fun setFavorite(messageIds: Set<String>)
    suspend fun unsetFavorite(messageIds: Set<String>)
    suspend fun getVisibilityPreference(): Boolean
    suspend fun setVisibilityPreference(value: Boolean)
}