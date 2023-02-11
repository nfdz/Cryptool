package io.github.nfdz.cryptool.shared.message.repository

import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import kotlinx.coroutines.delay

class FakeMessageReceiver(
    val receive1Exception: Throwable? = null,
) : MessageReceiver {

    var receive1Count = 0
    var receive1ArgEncryptionId: String? = null
    var receive1ArgEncryptedMessage: String? = null
    var receive1ArgIsRead: Boolean? = null
    override suspend fun receive(encryptionId: String, encryptedMessage: String, isRead: Boolean) {
        delay(50)
        receive1Count++
        receive1ArgEncryptionId = encryptionId
        receive1ArgEncryptedMessage = encryptedMessage
        receive1ArgIsRead = isRead
        receive1Exception?.let { throw it }
    }

    var receive2Count = 0
    var receive2ArgEncryption: Encryption? = null
    var receive2ArgEncryptedMessage: String? = null
    var receive2ArgTimestampInMillis: Long? = null
    var receive2ArgIsRead: Boolean? = null
    override suspend fun receive(
        encryption: Encryption,
        encryptedMessage: String,
        timestampInMillis: Long?,
        isRead: Boolean
    ) {
        delay(50)
        receive2Count++
        receive2ArgEncryption = encryption
        receive2ArgEncryptedMessage = encryptedMessage
        receive2ArgTimestampInMillis = timestampInMillis
        receive2ArgIsRead = isRead
    }
}