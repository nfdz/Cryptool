package io.github.nfdz.cryptool.shared.core.realm

import io.realm.kotlin.Realm

class FakeRealmGateway() : RealmGateway {

    val openRegistry: MutableList<ByteArray> = mutableListOf()
    override fun open(key: ByteArray) {
        openRegistry.add(key)
    }

    var tearDownCount = 0
    override fun tearDown() {
        tearDownCount++
    }

    override val instance: Realm
        get() = throw IllegalStateException()

    val executeOnOpenRegistry: MutableList<suspend (Realm) -> Unit> = mutableListOf()
    override fun executeOnOpen(callback: suspend (Realm) -> Unit) {
        executeOnOpenRegistry.add(callback)
    }
}