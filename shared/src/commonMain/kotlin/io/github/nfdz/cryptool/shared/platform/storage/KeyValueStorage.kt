package io.github.nfdz.cryptool.shared.platform.storage

interface KeyValueStorage {
    fun getString(key: String): String?
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun getInt(key: String, defaultValue: Int): Int
    fun putString(key: String, value: String)
    fun putBoolean(key: String, value: Boolean)
    fun putInt(key: String, value: Int)
    fun clear()
}