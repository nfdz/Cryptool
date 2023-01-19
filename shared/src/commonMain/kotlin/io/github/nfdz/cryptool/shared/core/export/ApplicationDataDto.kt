package io.github.nfdz.cryptool.shared.core.export

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.password.entity.Password
import kotlinx.serialization.Serializable

@Serializable
internal data class ApplicationDataDto(
    val v2: Boolean,
    val passwords: List<PasswordDto>,
    val encryptions: List<EncryptionDto>,
    val messages: List<MessageDto>,
)

@Serializable
internal data class EncryptionDto(
    val id: String,
    val name: String,
    val password: String,
    val algorithm: String,
    val source: String,
    val isFavorite: Boolean,
    val unreadMessagesCount: Int,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
) {
    companion object {
        fun from(value: Encryption): EncryptionDto {
            return EncryptionDto(
                id = value.id,
                name = value.name,
                password = value.password,
                algorithm = value.algorithm.name,
                source = value.source?.name ?: "",
                isFavorite = value.isFavorite,
                unreadMessagesCount = value.unreadMessagesCount,
                lastMessage = value.lastMessage,
                lastMessageTimestamp = value.lastMessageTimestamp,
            )
        }
    }

    fun toEntity(): Encryption = Encryption(
        id = id,
        name = name,
        password = password,
        algorithm = AlgorithmVersion.valueOf(algorithm),
        source = if (source.isNotBlank()) MessageSource.valueOf(source) else null,
        isFavorite = isFavorite,
        unreadMessagesCount = unreadMessagesCount,
        lastMessage = lastMessage,
        lastMessageTimestamp = lastMessageTimestamp,
    )
}

@Serializable
internal data class MessageDto(
    val id: String,
    val encryptionId: String,
    val message: String,
    val encryptedMessage: String,
    val timestampInMillis: Long,
    val isFavorite: Boolean,
    val ownership: String,
) {
    companion object {
        fun from(value: Message): MessageDto {
            return MessageDto(
                id = value.id,
                encryptionId = value.encryptionId,
                message = value.message,
                encryptedMessage = value.encryptedMessage,
                timestampInMillis = value.timestampInMillis,
                isFavorite = value.isFavorite,
                ownership = value.ownership.name,
            )
        }
    }

    fun toEntity(encryptionId: String): Message = Message(
        id = id,
        encryptionId = encryptionId,
        message = message,
        encryptedMessage = encryptedMessage,
        timestampInMillis = timestampInMillis,
        isFavorite = isFavorite,
        ownership = MessageOwnership.valueOf(ownership),
    )
}

@Serializable
internal data class PasswordDto(
    val id: String,
    val name: String,
    val password: String,
    val tags: String,
) {
    companion object {
        fun from(value: Password): PasswordDto {
            return PasswordDto(
                id = value.id,
                name = value.name,
                password = value.password,
                tags = Password.joinTags(value.tags),
            )
        }
    }

    fun toEntity(): Password {
        return Password(
            id = id,
            name = name,
            password = password,
            tags = Password.splitTags(tags),
        )
    }
}