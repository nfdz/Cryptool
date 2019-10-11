package io.github.nfdz.cryptool.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import io.github.nfdz.cryptool.views.cypher.CypherContract


class PreferencesHelper(private val context: Context) {

    companion object {
        private const val PREFS_FILE_NAME = "cryptool_preferences"
        private const val LAST_MODE_KEY = "last_mode"
        private const val LAST_PASSPHRASE_KEY = "last_passphrase"
        private const val LAST_PASSPHRASE_LOCKED_KEY = "last_passphrase_locked_flag"
        private const val LAST_ORIGIN_TEXT_KEY = "last_origin_text"
    }

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
//        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//        EncryptedSharedPreferences.create(
//            PREFS_FILE_NAME,
//            masterKeyAlias,
//            context,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
    }

    fun getLastMode(): CypherContract.ModeFlag {
        val lastModeString: String = preferences.getString(LAST_MODE_KEY, null) ?: ""
        return try {
            CypherContract.ModeFlag.valueOf(lastModeString)
        } catch (e: IllegalArgumentException) {
            CypherContract.DEFAULT_MODE
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setLastMode(mode: CypherContract.ModeFlag) {
        preferences.edit().putString(LAST_MODE_KEY, mode.name).commit()
    }

    fun getLastPassphrase(): String = preferences.getString(LAST_PASSPHRASE_KEY, null) ?: ""

    @SuppressLint("ApplySharedPref")
    fun setLastPassphrase(passphrase: String) {
        preferences.edit().putString(LAST_PASSPHRASE_KEY, passphrase).commit()
    }

    fun getLastOriginText(): String = preferences.getString(LAST_ORIGIN_TEXT_KEY, null) ?: ""

    @SuppressLint("ApplySharedPref")
    fun setLastOriginText(originText: String) {
        preferences.edit().putString(LAST_ORIGIN_TEXT_KEY, originText).commit()
    }

    fun wasLastPassphraseLocked(): Boolean = preferences.getBoolean(LAST_PASSPHRASE_LOCKED_KEY, false)

    @SuppressLint("ApplySharedPref")
    fun setLastPassphraseLocked(passphraseLocked: Boolean) {
        preferences.edit().putBoolean(LAST_PASSPHRASE_LOCKED_KEY, passphraseLocked).commit()
    }

}