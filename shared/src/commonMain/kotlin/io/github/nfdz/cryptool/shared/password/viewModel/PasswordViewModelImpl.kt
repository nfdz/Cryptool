package io.github.nfdz.cryptool.shared.password.viewModel

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.repository.PasswordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

class PasswordViewModelImpl(
    private val repository: PasswordRepository,
) : PasswordViewModelBase() {

    override val tag: String
        get() = "PasswordViewModel"

    override val initialState: PasswordState
        get() = PasswordState(false, emptyList(), emptySet(), emptyList())

    private var collectJob: Job? = null

    override suspend fun processAction(action: PasswordAction) {
        val previousState = currentState
        runCatching {
            when (action) {
                PasswordAction.Initialize -> collectPasswords()
                is PasswordAction.AddFilter -> addFilter(previousState, action.tag)
                is PasswordAction.RemoveFilter -> removeFilter(previousState, action.tag)
                is PasswordAction.Create -> createPassword(action)
                is PasswordAction.Edit -> editPassword(action)
                is PasswordAction.Remove -> removePassword(action.password)
            }
        }.onFailure {
            Napier.e(tag = tag, message = "processAction: $action", throwable = it)
        }
    }

    private suspend fun collectPasswords() {
        collectJob?.cancel()
        collectJob = launch {
            repository.observe().cancellable().collect {
                emitNewState(
                    currentState.copy(
                        initialized = true,
                        passwords = it.sorted(),
                        tags = it.getTags(),
                    )
                )
            }
        }
    }

    private fun addFilter(previousState: PasswordState, tag: String) {
        emitNewState(
            previousState.copy(
                selectedTags = previousState.selectedTags.toMutableSet().apply {
                    add(tag)
                },
            )
        )
    }

    private fun removeFilter(previousState: PasswordState, tag: String) {
        emitNewState(
            previousState.copy(
                selectedTags = previousState.selectedTags.toMutableSet().apply {
                    remove(tag)
                },
            )
        )
    }

    private suspend fun createPassword(action: PasswordAction.Create) {
        val password = repository.create(name = action.name, password = action.password, tags = action.tags)
        emitSideEffect(PasswordEffect.Created(password))
    }

    private suspend fun editPassword(action: PasswordAction.Edit) {
        val editedPassword = repository.edit(
            passwordToEdit = action.passwordToEdit,
            name = action.name,
            password = action.password,
            tags = action.tags,
        )
        emitSideEffect(PasswordEffect.Edited(editedPassword))
    }

    private suspend fun removePassword(password: Password) {
        repository.remove(password.id)
        emitSideEffect(PasswordEffect.Removed(password))
    }

    private fun List<Password>.getTags(): List<String> {
        return this.flatMap { it.tags }.toSet().sortedTags()
    }

    private fun Collection<Password>.sorted(): List<Password> {
        return sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }

    private fun Collection<String>.sortedTags(): List<String> {
        return sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
    }

}