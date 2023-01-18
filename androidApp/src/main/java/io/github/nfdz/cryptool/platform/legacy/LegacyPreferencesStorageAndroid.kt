package io.github.nfdz.cryptool.platform.legacy

import android.content.Context
import android.content.SharedPreferences
import io.github.nfdz.cryptool.shared.platform.cryptography.CryptographyV1
import io.github.nfdz.cryptool.shared.platform.storage.LegacyPreferencesStorage
import io.github.nfdz.cryptool.shared.platform.storage.legacyAssociateKeyValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LegacyPreferencesStorageAndroid(context: Context) : LegacyPreferencesStorage {

    companion object {
        private const val PREFS_FILE_NAME = "cryptool.prefs"

        private const val LAST_PASSPHRASE_KEY = "cipher_last_passphrase"
        private const val LAST_MODE_KEY = "cipher_last_mode"
        private const val LAST_ORIGIN_TEXT_KEY = "cipher_last_origin_text"
        private const val KEYS_LABEL_KEY = "keys_labels"
        private const val KEYS_VALUE_KEY = "keys_values"
        private const val ACCESS_CODE_KEY = "access_code"

        private const val DID_MIGRATION = "did_legacy_migration"
    }

    private val crypto = CryptographyV1()

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    private suspend fun exposeSensitiveField(storedField: String?): String = withContext(Dispatchers.Default) {
        runCatching {
            crypto.decrypt(
                password = LegacyPinCodeDialog.CODE,
                encryptedText = storedField!!
            )
        }.getOrNull() ?: ""
    }

    override fun isDecryptMode(): Boolean {
        val lastModeString: String = preferences.getString(LAST_MODE_KEY, null) ?: ""
        return lastModeString == "DECRYIPT_MODE"
    }

    override suspend fun getLastPassphrase(): String {
        return exposeSensitiveField(preferences.getString(LAST_PASSPHRASE_KEY, null))
    }

    override fun getLastOriginText(): String = preferences.getString(LAST_ORIGIN_TEXT_KEY, null) ?: ""

    override suspend fun getKeys(): Map<String, String> {
        return getKeysLabel().legacyAssociateKeyValue(getKeysValue())
    }

    private fun getKeysLabel(): Set<String> = preferences.getStringSet(KEYS_LABEL_KEY, null) ?: emptySet()

    private suspend fun getKeysValue(): Set<String> {
        val values = preferences.getStringSet(KEYS_VALUE_KEY, null) ?: emptySet()
        return values.map { value -> exposeSensitiveField(value) }.toHashSet()
    }

    override fun hasCode(): Boolean {
        return preferences.contains(ACCESS_CODE_KEY)
    }

    override suspend fun getCode(): String {
        return exposeSensitiveField(preferences.getString(ACCESS_CODE_KEY, null))
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.Default) {
            preferences.edit().clear().commit()
        }
    }

    override fun hasDataToMigrate(): Boolean {
        return preferences.contains(LAST_PASSPHRASE_KEY) || preferences.contains(LAST_MODE_KEY) || preferences.contains(
            LAST_ORIGIN_TEXT_KEY
        ) || preferences.contains(KEYS_LABEL_KEY) || preferences.contains(KEYS_VALUE_KEY) || preferences.contains(
            ACCESS_CODE_KEY
        )
    }

    override fun getDidMigration(): Boolean {
        return preferences.getLong(DID_MIGRATION, -1L) > 0L
    }

    override suspend fun setDidMigration() {
        withContext(Dispatchers.Default) {
            preferences.edit().putLong(DID_MIGRATION, System.currentTimeMillis()).commit()
        }
    }

}
