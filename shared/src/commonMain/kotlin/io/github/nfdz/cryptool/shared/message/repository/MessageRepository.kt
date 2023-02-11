package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.entity.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getAll(): List<Message>
    suspend fun addAll(messages: List<Message>)
    suspend fun observe(encryptionId: String): Flow<List<Message>>
    suspend fun sendMessage(encryptionId: String, message: String)
    suspend fun delete(messageIds: Set<String>)
    suspend fun setFavorite(messageIds: Set<String>)
    suspend fun unsetFavorite(messageIds: Set<String>)
    suspend fun getVisibilityPreference(): Boolean
    suspend fun setVisibilityPreference(value: Boolean)

    fun addOnSendMessageAction(action: (source: MessageSource, encryptedMessage: String) -> Unit)
}