package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.entity.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMessageRepository(
    private val getAllAnswer: List<Message> = emptyList(),
    private val observeAnswer: Flow<List<Message>> = flow { },
    private val getVisibilityAnswer: Boolean? = null,
    private val sendMessageException: Throwable? = null,
) : MessageRepository {

    var getAllCount = 0
    override fun getAll(): List<Message> {
        getAllCount++
        return getAllAnswer
    }

    var addAllRegistry = mutableListOf<List<Message>>()
    override suspend fun addAll(messages: List<Message>) {
        addAllRegistry.add(messages)
    }

    var observeCount = 0
    var observeArgEncryptionId: String? = null
    override suspend fun observe(encryptionId: String): Flow<List<Message>> {
        delay(150)
        observeCount++
        observeArgEncryptionId = encryptionId
        return observeAnswer
    }

    var sendMessageCount = 0
    var sendMessageArgEncryptionId: String? = null
    var sendMessageArgMessage: String? = null
    override suspend fun sendMessage(encryptionId: String, message: String) {
        delay(50)
        sendMessageCount++
        sendMessageArgEncryptionId = encryptionId
        sendMessageArgMessage = message
        sendMessageException?.let { throw it }
    }

    var deleteCount = 0
    var deleteArgIds: Set<String>? = null
    override suspend fun delete(messageIds: Set<String>) {
        deleteCount++
        deleteArgIds = messageIds
    }

    var setFavoriteCount = 0
    var setFavoriteArgIds: Set<String>? = null
    override suspend fun setFavorite(messageIds: Set<String>) {
        setFavoriteCount++
        setFavoriteArgIds = messageIds
    }

    var unsetFavoriteCount = 0
    var unsetFavoriteArgIds: Set<String>? = null
    override suspend fun unsetFavorite(messageIds: Set<String>) {
        unsetFavoriteCount++
        unsetFavoriteArgIds = messageIds
    }

    var getVisibilityCount = 0
    override suspend fun getVisibilityPreference(): Boolean {
        getVisibilityCount++
        return getVisibilityAnswer!!
    }

    var setVisibilityCount = 0
    var setVisibilityArg: Boolean? = null
    override suspend fun setVisibilityPreference(value: Boolean) {
        setVisibilityCount++
        setVisibilityArg = value
    }

    override fun addOnSendMessageAction(action: (source: MessageSource, encryptedMessage: String) -> Unit) {
        TODO("Not yet implemented")
    }
}