package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.encryption.entity.Encryption

interface MessageReceiver {
    suspend fun receive(
        encryptionId: String,
        encryptedMessage: String,
        isRead: Boolean,
    )
    suspend fun receive(
        encryption: Encryption,
        encryptedMessage: String,
        timestampInMillis: Long?,
        isRead: Boolean,
    )
}