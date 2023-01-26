package io.github.nfdz.cryptool.shared.gatekeeper.repository

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.core.realm.RealmGateway
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.encryption.repository.realm.EncryptionRealm
import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationData
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.repository.realm.MessageRealm
import io.github.nfdz.cryptool.shared.password.repository.realm.PasswordRealm
import io.github.nfdz.cryptool.shared.platform.storage.LegacyPreferencesStorage
import io.realm.kotlin.Realm

interface LegacyMigrationManager {
    suspend fun setDidMigration()
    fun canMigrate(): Boolean
    fun hasCode(): Boolean
    suspend fun getData(): LegacyMigrationData
    suspend fun doMigration(data: LegacyMigrationData)
}

class LegacyMigrationManagerImpl(
    private val prefs: LegacyPreferencesStorage,
    private val realmGateway: RealmGateway,
) : LegacyMigrationManager {

    private val realm: Realm
        get() = realmGateway.instance

    override suspend fun setDidMigration() {
        prefs.setDidMigration()
    }

    override fun canMigrate(): Boolean = !prefs.getDidMigration() && prefs.hasDataToMigrate()

    override fun hasCode(): Boolean = prefs.hasCode()

    override suspend fun getData(): LegacyMigrationData {
        return LegacyMigrationData(
            lastPassphrase = prefs.getLastPassphrase(),
            lastOriginText = prefs.getLastOriginText(),
            isDecryptMode = prefs.isDecryptMode(),
            keys = prefs.getKeys(),
        )
    }

    override suspend fun doMigration(data: LegacyMigrationData) {
        migrateEncryption(
            lastPassphrase = data.lastPassphrase,
            lastOriginText = data.lastOriginText,
            isDecryptMode = data.isDecryptMode,
        )
        migrateKeys(data.keys)
    }

    private suspend fun migrateEncryption(lastPassphrase: String, lastOriginText: String, isDecryptMode: Boolean) {
        if (lastPassphrase.isEmpty()) return

        runCatching {
            val encryptionEntry = EncryptionRealm.create(
                name = "Legacy conversation",
                password = lastPassphrase,
                algorithm = AlgorithmVersion.V1,
            ).apply {
                source = MessageSource.Manual.serialize()
            }
            val messageEntry = if (isDecryptMode) {
                val text = AlgorithmVersion.V1.createCryptography().decrypt(lastPassphrase, lastOriginText)
                MessageRealm.create(
                    encryptionId = encryptionEntry.id,
                    message = text ?: "",
                    encryptedMessage = lastOriginText,
                    ownership = MessageOwnership.OTHER,
                )
            } else {
                val encryptedMessage =
                    AlgorithmVersion.V1.createCryptography().encrypt(lastPassphrase, lastOriginText)
                MessageRealm.create(
                    encryptionId = encryptionEntry.id,
                    message = lastOriginText,
                    encryptedMessage = encryptedMessage ?: "",
                    ownership = MessageOwnership.OWN,
                )
            }

            realm.write {
                copyToRealm(encryptionEntry)
                copyToRealm(messageEntry)
            }
        }.onFailure {
            Napier.e(
                tag = "LegacyMigrationManager",
                message = "migrateEncryption($lastPassphrase, $lastOriginText, $isDecryptMode)",
                throwable = it
            )
        }
    }

    private suspend fun migrateKeys(keys: Map<String, String>) {
        runCatching {
            realm.write {
                keys.forEach {
                    copyToRealm(
                        PasswordRealm.create(
                            name = it.key,
                            password = it.value,
                            tags = "",
                        )
                    )
                }
            }
        }.onFailure {
            Napier.e(tag = "LegacyMigrationManager", message = "migrateKeys($keys)", throwable = it)
        }
    }

}