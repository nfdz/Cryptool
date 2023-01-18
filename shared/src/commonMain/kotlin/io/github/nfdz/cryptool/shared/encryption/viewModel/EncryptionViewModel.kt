package io.github.nfdz.cryptool.shared.encryption.viewModel

import io.github.nfdz.cryptool.shared.core.viewModel.*
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class EncryptionState(
    val initialized: Boolean,
    val encryptions: List<Encryption>,
    val selectedEncryptionIds: Set<String>,
) : State

sealed class EncryptionAction : Action {
    object Initialize : EncryptionAction()
    data class Create(val name: String, val password: String, val algorithm: AlgorithmVersion) : EncryptionAction()
    data class Edit(
        val encryptionToEdit: Encryption,
        val name: String,
        val password: String,
        val algorithm: AlgorithmVersion
    ) : EncryptionAction()

    data class Remove(val encryptionIds: Set<String>) : EncryptionAction()
    data class SetFavorite(val encryptionIds: Set<String>) : EncryptionAction()
    data class UnsetFavorite(val encryptionIds: Set<String>) : EncryptionAction()
    data class Select(val encryptionId: String) : EncryptionAction()
    data class Unselect(val encryptionId: String) : EncryptionAction()
    object SelectAll : EncryptionAction()
    object UnselectAll : EncryptionAction()
}

sealed class EncryptionEffect : Effect {
    data class Created(val encryption: Encryption) : EncryptionEffect()
    data class Edited(val encryption: Encryption) : EncryptionEffect()
    data class Removed(val ids: Set<String>) : EncryptionEffect()
    data class SetFavorite(val ids: Set<String>) : EncryptionEffect()
    data class UnsetFavorite(val ids: Set<String>) : EncryptionEffect()
}

interface EncryptionViewModel : NanoViewModel<EncryptionState, EncryptionAction, EncryptionEffect>
abstract class EncryptionViewModelBase : EncryptionViewModel,
    NanoViewModelBase<EncryptionState, EncryptionAction, EncryptionEffect>()

object EmptyEncryptionViewModel : EncryptionViewModel {
    override fun observeState(): StateFlow<EncryptionState> = throw IllegalStateException()
    override fun observeSideEffect(): Flow<EncryptionEffect> = throw IllegalStateException()
    override fun dispatch(action: EncryptionAction) {}
}
