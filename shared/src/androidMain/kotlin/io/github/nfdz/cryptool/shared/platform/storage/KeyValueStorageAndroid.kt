package io.github.nfdz.cryptool.shared.platform.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class KeyValueStorageAndroid(
    context: Context,
) : KeyValueStorage {

    companion object {
        private const val name = "secret_cryptool.prefs"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        name,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getString(key: String): String? = sharedPrefs.getString(key, null)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = sharedPrefs.getBoolean(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int): Int = sharedPrefs.getInt(key, defaultValue)

    override fun getLong(key: String, defaultValue: Long): Long = sharedPrefs.getLong(key, defaultValue)

    override fun putString(key: String, value: String) {
        sharedPrefs.edit().putString(key, value).apply()
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    override fun putInt(key: String, value: Int) {
        sharedPrefs.edit().putInt(key, value).apply()
    }

    override fun putLong(key: String, value: Long) {
        sharedPrefs.edit().putLong(key, value).apply()
    }

    override fun clear() {
        sharedPrefs.edit().clear().apply()
    }

}