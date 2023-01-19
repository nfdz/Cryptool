package io.github.nfdz.cryptool.shared.encryption.entity

data class Encryption(
    val id: String,
    val name: String,
    val password: String,
    val algorithm: AlgorithmVersion,
    val source: MessageSource?,
    val isFavorite: Boolean,
    val unreadMessagesCount: Int,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
)

const val minPasswordLength = 6
fun isEncryptionValid(name: String, password: String) = name.isNotEmpty() && password.length >= minPasswordLength