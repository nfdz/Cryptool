package io.github.nfdz.cryptool.shared.message.repository.realm

import io.github.nfdz.cryptool.shared.core.realm.RealmId
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.platform.time.Clock
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class MessageRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var encryptionId: String = ""
    var message: String = ""
    var encryptedMessage: String = ""
    var timestampInMillis: Long = 0L
    var isFavorite: Boolean = false
    var ownership: String = ""

    companion object {
        fun create(
            encryptionId: String,
            message: String,
            encryptedMessage: String,
            ownership: MessageOwnership,
        ): MessageRealm = MessageRealm().also { new ->
            new.id = RealmId.generateId()
            new.encryptionId = encryptionId
            new.message = message
            new.encryptedMessage = encryptedMessage
            new.timestampInMillis = Clock.nowInMillis()
            new.ownership = ownership.name
        }
    }

    fun toEntity(): Message = Message(
        id = id,
        encryptionId = encryptionId,
        message = message,
        encryptedMessage = encryptedMessage,
        timestampInMillis = timestampInMillis,
        isFavorite = isFavorite,
        ownership = MessageOwnership.valueOf(ownership),
    )
}
