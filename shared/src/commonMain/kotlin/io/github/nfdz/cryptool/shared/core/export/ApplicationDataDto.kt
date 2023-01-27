package io.github.nfdz.cryptool.shared.core.export

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.deserializeMessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.password.entity.Password
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApplicationDataDto(
    val v2: String,
    @SerialName("p")
    val passwords: List<PasswordDto>,
    @SerialName("e")
    val encryptions: List<EncryptionDto>,
    @SerialName("m")
    val messages: List<MessageDto>,
)

@Serializable
internal data class EncryptionDto(
    @SerialName("i")
    val id: String,
    @SerialName("n")
    val name: String,
    @SerialName("p")
    val password: String,
    @SerialName("a")
    val algorithm: String,
    @SerialName("s")
    val source: String,
    @SerialName("f")
    val isFavorite: Boolean,
    @SerialName("uc")
    val unreadMessagesCount: Int,
    @SerialName("lm")
    val lastMessage: String,
    @SerialName("lt")
    val lastMessageTimestamp: Long,
) {
    companion object {
        fun from(value: Encryption): EncryptionDto {
            return EncryptionDto(
                id = value.id,
                name = value.name,
                password = value.password,
                algorithm = value.algorithm.name,
                source = value.source?.serialize() ?: "",
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
        source = if (source.isNotBlank()) source.deserializeMessageSource() else null,
        isFavorite = isFavorite,
        unreadMessagesCount = unreadMessagesCount,
        lastMessage = lastMessage,
        lastMessageTimestamp = lastMessageTimestamp,
    )
}

@Serializable
internal data class MessageDto(
    @SerialName("i")
    val id: String,
    @SerialName("ei")
    val encryptionId: String,
    @SerialName("m")
    val message: String,
    @SerialName("em")
    val encryptedMessage: String,
    @SerialName("t")
    val timestampInMillis: Long,
    @SerialName("f")
    val isFavorite: Boolean,
    @SerialName("o")
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
    @SerialName("i")
    val id: String,
    @SerialName("n")
    val name: String,
    @SerialName("p")
    val password: String,
    @SerialName("t")
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