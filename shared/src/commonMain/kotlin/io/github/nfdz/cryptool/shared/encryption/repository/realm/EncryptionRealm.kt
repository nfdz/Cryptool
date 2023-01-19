package io.github.nfdz.cryptool.shared.encryption.repository.realm

import io.github.nfdz.cryptool.shared.core.realm.RealmId
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

internal class EncryptionRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var password: String = ""
    var algorithm: String = ""
    var source: String = ""
    var isFavorite: Boolean = false
    var unreadMessagesCount: Int = 0
    var lastMessage: String = ""
    var lastMessageTimestamp: Long = 0L

    companion object {
        fun create(name: String, password: String, algorithm: AlgorithmVersion) = EncryptionRealm().also { new ->
            new.id = RealmId.generateId()
            new.name = name
            new.password = password
            new.algorithm = algorithm.name
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
