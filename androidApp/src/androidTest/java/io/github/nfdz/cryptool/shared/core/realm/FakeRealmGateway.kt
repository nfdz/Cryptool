package io.github.nfdz.cryptool.shared.core.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class FakeRealmGateway() : RealmGateway {

    private val name = "test.realm"
    private val config = RealmConfiguration.Builder(realmSchema).name(name).build()
    private var _instance: Realm? = Realm.open(config)

    fun tearDownTest() {
        _instance!!.close()
        Realm.deleteRealm(config)
        _instance = null
    }

    override val instance: Realm
        get() = _instance!!

    var tearDownCount = 0
    override fun tearDown() {
        tearDownCount++
    }

    var openCount = 0
    var openArgKey: ByteArray? = null
    override fun open(key: ByteArray) {
        openCount++
        openArgKey = key
    }

    var executeOnOpenCount = 0
    var executeOnOpenArg: suspend (Realm) -> Unit = {}
    override fun executeOnOpen(callback: suspend (Realm) -> Unit) {
        executeOnOpenCount++
        executeOnOpenArg = callback
    }

}