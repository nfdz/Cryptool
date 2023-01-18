package io.github.nfdz.cryptool.shared.gatekeeper.viewModel

import io.github.nfdz.cryptool.shared.core.viewModel.*
import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.TutorialInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.WelcomeInformation
import io.github.nfdz.cryptool.shared.platform.biometric.BiometricContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class GatekeeperState(
    val isOpen: Boolean,
    val hasCode: Boolean,
    val welcome: WelcomeInformation?,
    val canUseBiometricAccess: Boolean,
    val canMigrateFromLegacy: LegacyMigrationInformation?,
    val loadingAccess: Boolean,
) : State

sealed class GatekeeperAction : Action {
    data class AccessWithCode(val code: String) : GatekeeperAction()
    data class AccessWithBiometric(val biometricContext: BiometricContext) : GatekeeperAction()
    data class Create(val code: String, val biometricEnabled: Boolean, val biometricContext: BiometricContext?) :
        GatekeeperAction()

    data class ChangeAccessCode(
        val oldCode: String,
        val newCode: String,
        val biometricEnabled: Boolean,
        val biometricContext: BiometricContext?
    ) : GatekeeperAction()

    object Delete : GatekeeperAction()
    data class AcknowledgeWelcome(val welcomeTutorial: TutorialInformation?) : GatekeeperAction()
    data class AcknowledgeLegacyMigration(val welcomeTutorial: TutorialInformation?, val migrateData: Boolean) :
        GatekeeperAction()

    object CheckAccess : GatekeeperAction()
    object PushAccessValidity : GatekeeperAction()
}

sealed class GatekeeperEffect : Effect {
    object ChangedCode : GatekeeperEffect()
    class Error(val message: String) : GatekeeperEffect()
}

interface GatekeeperViewModel : NanoViewModel<GatekeeperState, GatekeeperAction, GatekeeperEffect>
abstract class GatekeeperViewModelBase : GatekeeperViewModel,
    NanoViewModelBase<GatekeeperState, GatekeeperAction, GatekeeperEffect>()

const val minCodeLength = 8
fun isCodeValid(code: String) = code.length >= minCodeLength

object EmptyGatekeeperViewModel : GatekeeperViewModel {
    override fun observeState(): StateFlow<GatekeeperState> = throw IllegalStateException()
    override fun observeSideEffect(): Flow<GatekeeperEffect> = throw IllegalStateException()
    override fun dispatch(action: GatekeeperAction) {}
}
