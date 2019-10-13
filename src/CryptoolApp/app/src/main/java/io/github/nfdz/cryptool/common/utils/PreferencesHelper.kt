package io.github.nfdz.cryptool.common.utils

import android.content.Context
import android.content.SharedPreferences
import io.github.nfdz.cryptool.services.BallService
import io.github.nfdz.cryptool.views.cipher.CipherContract


class PreferencesHelper(private val context: Context) {

    companion object {
        private const val PREFS_FILE_NAME = "cryptool_preferences"
        private const val LAST_MODE_KEY = "last_mode_key"
        private const val LAST_PASSPHRASE_KEY = "last_passphrase"
        private const val LAST_PASSPHRASE_LOCKED_KEY = "last_passphrase_locked_flag"
        private const val LAST_ORIGIN_TEXT_KEY = "last_origin_text"
        private const val LAST_BALL_POSITION_KEY = "last_ball_position"
        private const val LAST_BALL_GRAVITY_KEY = "last_ball_gravity"
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

    fun getLastPassphrase(): String = preferences.getString(LAST_PASSPHRASE_KEY, null) ?: ""

    fun setLastPassphrase(passphrase: String) {
        preferences.edit().putString(LAST_PASSPHRASE_KEY, passphrase).apply()
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

}