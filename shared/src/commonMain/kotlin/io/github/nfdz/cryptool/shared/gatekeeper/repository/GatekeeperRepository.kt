package io.github.nfdz.cryptool.shared.gatekeeper.repository

import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.TutorialInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.WelcomeInformation
import io.github.nfdz.cryptool.shared.platform.biometric.BiometricContext

interface GatekeeperRepository {
    fun isOpen(): Boolean
    fun hasCode(): Boolean
    fun canUseBiometricAccess(): Boolean
    fun canMigrateFromLegacy(): LegacyMigrationInformation?
    suspend fun setNewCode(code: String, biometricEnabled: Boolean)
    fun setBiometricAccess(enabled: Boolean)
    fun reset()
    fun checkAccessChange(): Boolean
    fun pushAccessValidity()
    suspend fun validateCode(code: String): Boolean
    suspend fun biometricAccess(context: BiometricContext): Boolean
    fun acknowledgeWelcome(welcomeTutorial: TutorialInformation?)
    fun getWelcomeInformation(): WelcomeInformation?
    suspend fun launchMigration()
    suspend fun encryptWithAccessCode(text: String): String?
    suspend fun decryptWithAccessCode(encryptedText: String): String?
    fun addOnOpenAction(action: () -> Unit)
    fun addOnResetAction(action: () -> Unit)
}