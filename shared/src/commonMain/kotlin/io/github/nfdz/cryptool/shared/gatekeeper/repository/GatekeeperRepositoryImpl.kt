package io.github.nfdz.cryptool.shared.gatekeeper.repository

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.core.realm.RealmGateway
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.encryption.repository.realm.EncryptionRealm
import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationData
import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.TutorialInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.WelcomeInformation
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.repository.realm.MessageRealm
import io.github.nfdz.cryptool.shared.platform.biometric.Biometric
import io.github.nfdz.cryptool.shared.platform.biometric.BiometricContext
import io.github.nfdz.cryptool.shared.platform.cryptography.Argon2KeyDerivation
import io.github.nfdz.cryptool.shared.platform.cryptography.decodeBase64
import io.github.nfdz.cryptool.shared.platform.cryptography.encodeBase64
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.github.nfdz.cryptool.shared.platform.time.Clock
import io.github.nfdz.cryptool.shared.platform.version.ChangelogProvider
import io.github.nfdz.cryptool.shared.platform.version.VersionProvider
import io.realm.kotlin.Realm

class GatekeeperRepositoryImpl(
    private val storage: KeyValueStorage,
    private val biometric: Biometric,
    private val changelogProvider: ChangelogProvider,
    private val realmGateway: RealmGateway,
    private val legacyMigrationManager: LegacyMigrationManager,
    private val versionProvider: VersionProvider,
) : GatekeeperRepository {

    companion object {
        const val codeKey = "access_code"
        const val codeSaltKey = "access_code_salt"
        const val biometricCodeKey = "access_biometric_code_v2"

        private const val accessValidityPeriodInSeconds = 300 // 5 min
    }

    private val keyDerivation = Argon2KeyDerivation()
    private val cryptography = AlgorithmVersion.V2.createCryptography()
    private var migrationInProgress: LegacyMigrationData? = null
    private var activeCode: String? = null
    private var accessValidityTimestampInSeconds = 0L
    private val onOpenActions = mutableListOf<() -> Unit>()
    private val onResetActions = mutableListOf<() -> Unit>()

    override fun isOpen(): Boolean = activeCode != null

    override fun hasCode(): Boolean = storage.getString(codeKey) != null

    override fun canUseBiometricAccess(): Boolean = storage.getString(biometricCodeKey).isNullOrBlank().not()

    override fun canMigrateFromLegacy(): LegacyMigrationInformation? {
        return if (migrationInProgress == null && legacyMigrationManager.canMigrate()) {
            LegacyMigrationInformation(hasCode = legacyMigrationManager.hasCode())
        } else {
            null
        }
    }

    override suspend fun setNewCode(code: String, biometricEnabled: Boolean, ) {
        val encryptedCode = cryptography.encrypt(password = code, text = code) ?: return
        setBiometricAccess(code, biometricEnabled)
        storage.putString(codeKey, encryptedCode)
        storage.putString(codeSaltKey, keyDerivation.generateSalt().encodeBase64())
        setupActiveCode(code)
    }

    override fun setBiometricAccess(enabled: Boolean) {
        activeCode?.let {
            setBiometricAccess(it, enabled)
        }
    }

    private fun setBiometricAccess(code: String, enabled: Boolean) {
        if (enabled) {
            storage.putString(biometricCodeKey, code)
        } else {
            storage.putString(biometricCodeKey, "")
        }
    }

    override fun checkAccessChange(): Boolean {
        Napier.d(tag = "GatekeeperRepository", message = "Check access validity")
        if (activeCode == null) return false
        val elapsedTime = Clock.nowInSeconds() - accessValidityTimestampInSeconds
        return if (elapsedTime > accessValidityPeriodInSeconds) {
            Napier.d(tag = "GatekeeperRepository", message = "Access is no longer valid")
            activeCode = null
            realmGateway.close()
            true
        } else {
            false
        }
    }

    override fun pushAccessValidity() {
        Napier.d(tag = "GatekeeperRepository", message = "Push access validity")
        accessValidityTimestampInSeconds = Clock.nowInSeconds()
    }

    override fun reset() {
        activeCode = null
        storage.clear().also { acknowledgeWelcome(null) }
        realmGateway.tearDown()
        onResetActions.forEach { it() }
    }

    override suspend fun biometricAccess(context: BiometricContext): Boolean {
        biometric.authenticate(context)
        val code = storage.getString(biometricCodeKey) ?: return false
        return validateCode(code)
    }

    override suspend fun validateCode(code: String): Boolean {
        val encryptedCode = storage.getString(codeKey) ?: return false
        val plainCode =
            cryptography.decrypt(password = code, encryptedText = encryptedCode) ?: return false
        val isValid = code == plainCode
        if (isValid) setupActiveCode(code)
        return isValid
    }

    private suspend fun setupActiveCode(code: String) {
        val wasOpen = isOpen()
        val salt = storage.getString(codeSaltKey)?.decodeBase64()
            ?: throw IllegalStateException("Salt is missing")
        val key = keyDerivation.hash(code, salt, RealmGateway.keyHashLength)
        realmGateway.open(key)
        activeCode = code
        finishMigrationInProgress()
        accessValidityTimestampInSeconds = Clock.nowInSeconds()
        if (!wasOpen) {
            onOpenActions.forEach { it() }
        }
    }

    override fun acknowledgeWelcome(welcomeTutorial: TutorialInformation?) {
        versionProvider.storedVersion = versionProvider.appVersion
        if (welcomeTutorial != null) {
            realmGateway.executeOnOpen {
                injectTutorial(it, welcomeTutorial)
            }
        }
    }

    private suspend fun injectTutorial(realm: Realm, welcomeTutorial: TutorialInformation) {
        runCatching {
            val timestamp = Clock.nowInMillis()
            val messages = welcomeTutorial.messages.map {
                it to (cryptography.encrypt(TutorialInformation.defaultPassword, it)!!)
            }
            realm.write {
                val encryption = copyToRealm(
                    EncryptionRealm.create(
                        name = welcomeTutorial.title,
                        password = TutorialInformation.defaultPassword,
                        algorithm = cryptography.version,
                    ).apply {
                        source = MessageSource.Manual.serialize()
                        lastMessage = messages.first().second
                        lastMessageTimestamp = timestamp
                        unreadMessagesCount = welcomeTutorial.messages.size
                    }
                )
                messages.forEachIndexed { index, message ->
                    copyToRealm(
                        MessageRealm.create(
                            encryptionId = encryption.id,
                            message = message.first,
                            encryptedMessage = message.second,
                            ownership = MessageOwnership.OTHER,
                        ).apply {
                            this.timestampInMillis = timestamp - index
                        }
                    )
                }
            }
        }.onFailure {
            Napier.e(
                tag = "GatekeeperRepository",
                message = "Inject tutorial error: ${it.message}",
                throwable = it
            )
        }
    }

    override suspend fun launchMigration() {
        val data = legacyMigrationManager.getData()
        if (data.isNotEmpty()) {
            migrationInProgress = data
        } else {
            legacyMigrationManager.setDidMigration()
        }
    }

    override suspend fun encryptWithAccessCode(text: String): String? {
        val code = activeCode ?: throw IllegalStateException("Not open")
        return cryptography.encrypt(code, text)
    }

    override suspend fun decryptWithAccessCode(encryptedText: String): String? {
        val code = activeCode ?: throw IllegalStateException("Not open")
        return cryptography.decrypt(code, encryptedText)
    }

    override fun addOnOpenAction(action: () -> Unit) {
        onOpenActions.add(action)
    }

    override fun addOnResetAction(action: () -> Unit) {
        onResetActions.add(action)
    }

    private suspend fun finishMigrationInProgress() {
        migrationInProgress?.let {
            legacyMigrationManager.doMigration(it)
            legacyMigrationManager.setDidMigration()
        }
        migrationInProgress = null
    }

    override fun getWelcomeInformation(): WelcomeInformation? {
        return when (val storedVersion = versionProvider.storedVersion) {
            versionProvider.appVersion -> null
            VersionProvider.noVersion -> WelcomeInformation(
                title = changelogProvider.mainTitle,
                content = changelogProvider.mainContent,
                welcomeTutorial = true,
            )
            else -> {
                val changelog = changelogProvider.summary(storedVersion)
                if (changelog.isEmpty()) {
                    null
                } else {
                    WelcomeInformation(
                        title = changelogProvider.changelogTitle,
                        content = changelog,
                        welcomeTutorial = false,
                    )
                }
            }
        }
    }

}