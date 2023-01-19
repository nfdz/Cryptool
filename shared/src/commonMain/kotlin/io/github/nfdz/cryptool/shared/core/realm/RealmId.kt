package io.github.nfdz.cryptool.shared.core.realm

import io.realm.kotlin.types.RealmUUID

object RealmId {
    fun generateId(): String = RealmUUID.random().toString()
}