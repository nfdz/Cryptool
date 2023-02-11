package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.core.realm.RealmGateway
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.deserializeMessageSource
import io.github.nfdz.cryptool.shared.encryption.repository.realm.EncryptionRealm
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.repository.realm.MessageRealm
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class MessageRepositoryImpl(
    private val realmGateway: RealmGateway,
    private val storage: KeyValueStorage,
) : MessageRepository {
    companion object {
        const val visibilityKey = "preference_visibility"
        const val defaultVisibility = true
    }

    private val realm: Realm
        get() = realmGateway.instance
    private val onSendMessageActions = mutableListOf<(source: MessageSource, encryptedMessage: String) -> Unit>()

    override fun getAll(): List<Message> {
        return realm.query<MessageRealm>().find().map {
            it.toEntity()
        }
    }

    override suspend fun addAll(messages: List<Message>) {
        realm.write {
            messages.forEach {
                copyToRealm(
                    MessageRealm().apply {
                        id = it.id
                        encryptionId = it.encryptionId
                        message = it.message
                        encryptedMessage = it.encryptedMessage
                        timestampInMillis = it.timestampInMillis
                        isFavorite = it.isFavorite
                        ownership = it.ownership.name
                    }, UpdatePolicy.ALL
                )
            }
        }
    }

    override suspend fun observe(encryptionId: String): Flow<List<Message>> {
        return realm.query<MessageRealm>("encryptionId == '${encryptionId}'").asFlow().transform { value ->
            emit(value.list.map { it.toEntity() })
        }
    }

    override suspend fun sendMessage(encryptionId: String, message: String) {
        if (encryptionId.isBlank()) return
        val encryptionEntry = realm.query<EncryptionRealm>("id == '${encryptionId}'").find().first()
        val cryptography = AlgorithmVersion.valueOf(encryptionEntry.algorithm).createCryptography()
        val encryptedMessage = cryptography.encrypt(password = encryptionEntry.password, text = message)
            ?: throw IllegalStateException("Cannot receive message")
        val encryption = realm.query<EncryptionRealm>("id == '${encryptionId}'").find().first()
        val source = encryption.source.deserializeMessageSource()
        onSendMessageActions.forEach { it(source, encryptedMessage) }

        return realm.write {
            val entry = copyToRealm(
                MessageRealm.create(
                    encryptionId = encryptionId,
                    message = message,
                    encryptedMessage = encryptedMessage,
                    ownership = MessageOwnership.OWN,
                )
            )

            query<EncryptionRealm>("id == '${encryptionId}'").find().first().apply {
                this.lastMessage = encryptedMessage
                this.lastMessageTimestamp = entry.timestampInMillis
            }
        }
    }

    override suspend fun delete(messageIds: Set<String>) {
        realm.write {
            messageIds.forEach {
                delete(query<MessageRealm>("id == '${it}'").find().first())
            }
        }
    }

    override suspend fun setFavorite(messageIds: Set<String>) = setFavorite(messageIds, true)

    override suspend fun unsetFavorite(messageIds: Set<String>) = setFavorite(messageIds, false)

    private suspend fun setFavorite(messageIds: Set<String>, state: Boolean) {
        return realm.write {
            messageIds.forEach {
                query<MessageRealm>("id == '${it}'").find().first().apply {
                    this.isFavorite = state
                }
            }
        }
    }

    override suspend fun getVisibilityPreference(): Boolean {
        return storage.getBoolean(visibilityKey, defaultVisibility)
    }

    override suspend fun setVisibilityPreference(value: Boolean) {
        storage.putBoolean(visibilityKey, value)
    }

    override fun addOnSendMessageAction(action: (source: MessageSource, encryptedMessage: String) -> Unit) {
        onSendMessageActions.add(action)
    }

}