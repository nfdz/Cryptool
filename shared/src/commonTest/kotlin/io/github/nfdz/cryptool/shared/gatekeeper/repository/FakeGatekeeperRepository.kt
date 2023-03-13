package io.github.nfdz.cryptool.shared.gatekeeper.repository

import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.TutorialInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.WelcomeInformation
import io.github.nfdz.cryptool.shared.platform.biometric.BiometricContext
import kotlinx.coroutines.delay

class FakeGatekeeperRepository(
    val isOpenAnswer: List<Boolean> = emptyList(),
    val hasCodeAnswer: Boolean? = null,
    val getWelcomeInformationAnswer: WelcomeInformation? = null,
    val canMigrateFromLegacyAnswer: LegacyMigrationInformation? = null,
    val validateCodeAnswer: Boolean? = null,
    val checkAccessAnswer: Boolean? = null,
) : GatekeeperRepository {

    var isOpenCount = 0
    override fun isOpen(): Boolean {
        return isOpenAnswer[isOpenCount].also {
            isOpenCount++
        }
    }

    override fun hasCode(): Boolean {
        return hasCodeAnswer!!
    }

    override fun canUseBiometricAccess(): Boolean {
        return false
    }

    override fun canMigrateFromLegacy(): LegacyMigrationInformation? {
        return canMigrateFromLegacyAnswer
    }

    var setNewCodeCount = 0
    var setNewCodeArgCode: String? = null
    override suspend fun setNewCode(code: String, biometricEnabled: Boolean) {
        setNewCodeCount++
        setNewCodeArgCode = code
    }

    var setBiometricAccessCount = 0
    var setBiometricAccessArg: Boolean? = null
    override fun setBiometricAccess(enabled: Boolean) {
        setBiometricAccessCount++
        setBiometricAccessArg = enabled
    }

    var resetCount = 0
    override fun reset() {
        resetCount++
    }

    var checkAccessCount = 0
    override fun checkAccessChange(): Boolean {
        checkAccessCount++
        return checkAccessAnswer!!
    }

    var pushAccessValidity = 0
    override fun pushAccessValidity() {
        pushAccessValidity++
    }

    var validateCodeCount = 0
    var validateCodeArgCode: String? = null
    override suspend fun validateCode(code: String): Boolean {
        delay(50)
        validateCodeCount++
        validateCodeArgCode = code
        return validateCodeAnswer!!
    }

    override suspend fun biometricAccess(context: BiometricContext): Boolean {
        TODO("Not yet implemented")
    }

    var acknowledgeWelcomeCount = 0
    var acknowledgeWelcomeArgTutorial: TutorialInformation? = null
    override fun acknowledgeWelcome(welcomeTutorial: TutorialInformation?) {
        acknowledgeWelcomeCount++
        acknowledgeWelcomeArgTutorial = welcomeTutorial
    }

    override fun getWelcomeInformation(): WelcomeInformation? {
        return getWelcomeInformationAnswer
    }

    var launchMigrationCount = 0
    override suspend fun launchMigration() {
        launchMigrationCount++
    }

    override suspend fun encryptWithAccessCode(text: String): String? {
        TODO("Not yet implemented")
    }

    override suspend fun decryptWithAccessCode(encryptedText: String): String? {
        TODO("Not yet implemented")
    }

    override fun addOnOpenAction(action: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun addOnResetAction(action: () -> Unit) {
        TODO("Not yet implemented")
    }
}