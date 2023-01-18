package io.github.nfdz.cryptool.shared.password.repository.realm

import io.github.nfdz.cryptool.shared.core.realm.RealmId
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

internal class PasswordRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var password: String = ""
    var tags: String = ""

    companion object {
        fun create(name: String, password: String, tags: String): PasswordRealm = PasswordRealm().also { new ->
            new.id = RealmId.generateId()
            new.name = name
            new.password = password
            new.tags = tags
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
