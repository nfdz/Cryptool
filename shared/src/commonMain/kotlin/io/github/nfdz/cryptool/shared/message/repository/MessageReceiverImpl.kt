package io.github.nfdz.cryptool.shared.message.repository

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.core.realm.RealmGateway
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.repository.realm.EncryptionRealm
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.repository.realm.MessageRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class MessageReceiverImpl(
    private val realmGateway: RealmGateway,
) : MessageReceiver {

    companion object {
        private const val tag = "MessageReceiver"
    }

    private val realm: Realm
        get() = realmGateway.instance

    override suspend fun receive(encryptionId: String, encryptedMessage: String, isRead: Boolean) {
        Napier.d(tag = tag, message = "[receiveReadMessage] Message from '$encryptionId: '${encryptedMessage}")
        if (encryptionId.isBlank()) throw IllegalStateException("Cannot receive message - no encryption ID")
        val encryptionEntry = realm.query<EncryptionRealm>("id == '${encryptionId}'").find().first()
        val cryptography = AlgorithmVersion.valueOf(encryptionEntry.algorithm).createCryptography()
        val message = cryptography.decrypt(password = encryptionEntry.password, encryptedText = encryptedMessage)
            ?: throw IllegalStateException("Cannot receive message - cannot decrypt")
        receiveMessageInternal(
            encryptionId = encryptionId,
            message = message,
            encryptedMessage = encryptedMessage,
            timestampInMillis = null,
            countUnread = !isRead
        )
    }

    override suspend fun receive(
        encryption: Encryption,
        encryptedMessage: String,
        timestampInMillis: Long?,
        isRead: Boolean,
    ) {
        Napier.d(tag = tag, message = "[receivePendingMessage] Message from '${encryption.name}: '${encryptedMessage}")
        val cryptography = encryption.algorithm.createCryptography()
        val message = cryptography.decrypt(encryption.password, encryptedMessage)
            ?: throw IllegalStateException("Cannot process the message")
        receiveMessageInternal(
            encryptionId = encryption.id,
            message = message,
            encryptedMessage = encryptedMessage,
            timestampInMillis = timestampInMillis,
            countUnread = !isRead
        )
    }

    private suspend fun receiveMessageInternal(
        encryptionId: String,
        message: String,
        encryptedMessage: String,
        timestampInMillis: Long?,
        countUnread: Boolean
    ) {
        realm.write {
            val entry = copyToRealm(
                MessageRealm.create(
                    encryptionId = encryptionId,
                    message = message,
                    encryptedMessage = encryptedMessage,
                    ownership = MessageOwnership.OTHER,
                ).also { new ->
                    timestampInMillis?.let { new.timestampInMillis = it }
                }
            )
            query<EncryptionRealm>("id == '${encryptionId}'").find().first().apply {
                this.lastMessage = "$name: $encryptedMessage"
                this.lastMessageTimestamp = entry.timestampInMillis
                if (countUnread) {
                    this.unreadMessagesCount++
                }
            }
        }
    }
}