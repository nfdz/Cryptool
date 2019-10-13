package io.github.nfdz.cryptool.common.utils

import android.content.Context
import android.content.SharedPreferences
import io.github.nfdz.cryptool.services.BallService
import io.github.nfdz.cryptool.views.cipher.CipherContract
import timber.log.Timber
import java.lang.Exception


class PreferencesHelper(private val context: Context) {

    companion object {
        private const val PREFS_FILE_NAME = "cryptool_preferences"
        private const val LAST_TAB_KEY = "last_tab"
        private const val LAST_MODE_KEY = "cipher_last_mode"
        private const val LAST_PASSPHRASE_KEY = "cipher_last_passphrase"
        private const val LAST_PASSPHRASE_LOCKED_KEY = "cipher_last_passphrase_locked_flag"
        private const val LAST_ORIGIN_TEXT_KEY = "cipher_last_origin_text"
        private const val LAST_BALL_POSITION_KEY = "last_ball_position"
        private const val LAST_BALL_GRAVITY_KEY = "last_ball_gravity"
        private const val LAST_HASH_ORIGIN_TEXT_KEY = "hash_hash_origin_text"
    }

    private val crypto = CryptographyHelper()

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        // TODO: Use EncryptedSharedPreferences when sdk min is ok and lib is not alpha
        // WORKAROUND: Use own AES encryption for sensitive fields meanwhile
//        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//        EncryptedSharedPreferences.create(
//            PREFS_FILE_NAME,
//            masterKeyAlias,
//            context,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
    }

    private fun hideSensitiveField(field: String?): String {
        return if (field?.isNotEmpty() == true) {
            try {
                crypto.encrypt(field, PREFS_FILE_NAME)
            } catch (e: Exception) {
                Timber.e(e)
                ""
            }
        } else {
            ""
        }
    }

    private fun exposeSensitiveField(storedField: String?): String {
        return if (storedField?.isNotEmpty() == true) {
            try {
                crypto.decrypt(storedField, PREFS_FILE_NAME)
            } catch (e: Exception) {
                Timber.e(e)
                ""
            }
        } else {
            ""
        }
    }

    fun getLastMode(): CipherContract.ModeFlag {
        val lastModeString: String = preferences.getString(LAST_MODE_KEY, null) ?: ""
        return try {
            CipherContract.ModeFlag.valueOf(lastModeString)
        } catch (e: IllegalArgumentException) {
            CipherContract.DEFAULT_MODE
        }
    }

    fun setLastMode(mode: CipherContract.ModeFlag) {
        preferences.edit().putString(LAST_MODE_KEY, mode.name).apply()
    }

    fun getLastPassphrase(): String {
        return exposeSensitiveField(preferences.getString(LAST_PASSPHRASE_KEY, null))
    }

    fun setLastPassphrase(passphrase: String) {
        preferences.edit().putString(LAST_PASSPHRASE_KEY, hideSensitiveField(passphrase)).apply()
    }

    fun getLastOriginText(): String = preferences.getString(LAST_ORIGIN_TEXT_KEY, null) ?: ""

    fun setLastOriginText(originText: String) {
        preferences.edit().putString(LAST_ORIGIN_TEXT_KEY, originText).apply()
    }

    fun wasLastPassphraseLocked(): Boolean =
        preferences.getBoolean(LAST_PASSPHRASE_LOCKED_KEY, false)

    fun setLastPassphraseLocked(passphraseLocked: Boolean) {
        preferences.edit().putBoolean(LAST_PASSPHRASE_LOCKED_KEY, passphraseLocked).apply()
    }

    fun getLastBallPosition(): Int = preferences.getInt(LAST_BALL_POSITION_KEY, 0)

    fun getLastBallGravity(): Int = preferences.getInt(LAST_BALL_GRAVITY_KEY, BallService.DEFAULT_GRAVITY)

    fun setLastBallPosition(position: Int) {
        preferences.edit().putInt(LAST_BALL_POSITION_KEY, position).apply()
    }

    fun setLastBallGravity(gravity: Int) {
        preferences.edit().putInt(LAST_BALL_GRAVITY_KEY, gravity).apply()
    }

    fun getLastHashOriginText(): String = preferences.getString(LAST_HASH_ORIGIN_TEXT_KEY, null) ?: ""

    fun setLastHashOriginText(originText: String) {
        preferences.edit().putString(LAST_HASH_ORIGIN_TEXT_KEY, originText).apply()
    }

    fun getLastTab(): Int = preferences.getInt(LAST_TAB_KEY, 0)

    fun setLastTab(tabIndex: Int) {
        preferences.edit().putInt(LAST_TAB_KEY, tabIndex).apply()
    }

}