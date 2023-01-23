package io.github.nfdz.cryptool.shared.platform.storage

class FakeKeyValueStorage : KeyValueStorage {
    val map = mutableMapOf<String, Any>()

    override fun getString(key: String): String? {
        return map[key] as? String?
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return map[key] as? Boolean? ?: defaultValue
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return map[key] as? Int? ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        map[key] = value
    }

    override fun putBoolean(key: String, value: Boolean) {
        map[key] = value
    }

    override fun putInt(key: String, value: Int) {
        map[key] = value
    }

    override fun clear() {
        map.clear()
    }
}