package io.github.nfdz.cryptool.shared.message.entity

data class Message(
    val id: String,
    val encryptionId: String,
    val message: String,
    val encryptedMessage: String,
    val timestampInMillis: Long,
    val isFavorite: Boolean,
    val ownership: MessageOwnership,
)