package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.message.entity.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMessageRepository(
    var getAllAnswer: List<Message> = emptyList(),
    var observeAnswer: Flow<List<Message>> = flow { },
    var getVisibilityAnswer: Boolean? = null,
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
        delay(50)
        observeCount++
        observeArgEncryptionId = encryptionId
        return observeAnswer
    }

    var receiveMessageCount = 0
    var receiveMessageArgEncryptionId: String? = null
    var receiveMessageArgEncryptedMessage: String? = null
    override suspend fun receiveMessage(encryptionId: String, encryptedMessage: String) {
        delay(50)
        receiveMessageCount++
        receiveMessageArgEncryptionId = encryptionId
        receiveMessageArgEncryptedMessage = encryptedMessage
    }

    var sendMessageCount = 0
    var sendMessageArgEncryptionId: String? = null
    var sendMessageArgMessage: String? = null
    override suspend fun sendMessage(encryptionId: String, message: String) {
        delay(50)
        sendMessageCount++
        sendMessageArgEncryptionId = encryptionId
        sendMessageArgMessage = message
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
}