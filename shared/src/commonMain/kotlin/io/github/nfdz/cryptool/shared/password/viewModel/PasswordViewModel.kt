package io.github.nfdz.cryptool.shared.password.viewModel

import io.github.nfdz.cryptool.shared.core.viewModel.*
import io.github.nfdz.cryptool.shared.password.entity.Password
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class PasswordState(
    val initialized: Boolean,
    val passwords: List<Password>,
    val selectedTags: Set<String>,
    val tags: List<String>,
) : State

sealed class PasswordAction : Action {
    object Initialize : PasswordAction()
    data class Create(val name: String, val password: String, val tags: String) : PasswordAction()
    data class Edit(val passwordToEdit: Password, val name: String, val password: String, val tags: String) :
        PasswordAction()

    data class Remove(val password: Password) : PasswordAction()
    data class AddFilter(val tag: String) : PasswordAction()
    data class RemoveFilter(val tag: String) : PasswordAction()
}

sealed class PasswordEffect : Effect {
    data class Created(val password: Password) : PasswordEffect()
    data class Edited(val password: Password) : PasswordEffect()
    data class Removed(val password: Password) : PasswordEffect()
}

interface PasswordViewModel : NanoViewModel<PasswordState, PasswordAction, PasswordEffect>
abstract class PasswordViewModelBase : PasswordViewModel,
    NanoViewModelBase<PasswordState, PasswordAction, PasswordEffect>()

fun isPasswordValid(name: String, password: String) = name.isNotEmpty() && password.isNotEmpty()

val PasswordState.filteredPassword: List<Password>
    get() {
        val validSelectedTags = selectedTags.intersect(tags.toSet())
        return if (validSelectedTags.isEmpty()) {
            passwords
        } else {
            passwords.filter { password -> password.tags.any { tag -> validSelectedTags.contains(tag) } }
        }
    }

object EmptyPasswordViewModel : PasswordViewModel {
    override fun observeState(): StateFlow<PasswordState> = throw IllegalStateException()
    override fun observeSideEffect(): Flow<PasswordEffect> = throw IllegalStateException()
    override fun dispatch(action: PasswordAction) {}
}
