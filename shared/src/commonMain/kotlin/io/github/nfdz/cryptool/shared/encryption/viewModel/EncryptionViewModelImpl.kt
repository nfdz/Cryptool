package io.github.nfdz.cryptool.shared.encryption.viewModel

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.message.repository.MessageReceiver
import io.github.nfdz.cryptool.shared.platform.localization.LocalizedError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

class EncryptionViewModelImpl(
    private val repository: EncryptionRepository,
    private val localizedError: LocalizedError,
    private val messageReceiver: MessageReceiver,
) : EncryptionViewModelBase() {

    override val tag: String
        get() = "EncryptionViewModel"

    override val initialState: EncryptionState
        get() = EncryptionState(false, emptyList(), emptySet(), null)

    private var incomingData: String? = null
    private var collectJob: Job? = null
    override suspend fun processAction(action: EncryptionAction) {
        val previousState = currentState
        runCatching {
            when (action) {
                EncryptionAction.Initialize -> collectEncryptions()
                is EncryptionAction.Create -> createEncryption(action)
                is EncryptionAction.Edit -> editEncryption(action)
                is EncryptionAction.Remove -> removeEncryption(action.encryptionIds)
                is EncryptionAction.Select -> selectEncryption(previousState, action.encryptionId)
                is EncryptionAction.Unselect -> unselectEncryption(previousState, action.encryptionId)
                EncryptionAction.UnselectAll -> unselectAll(previousState)
                EncryptionAction.SelectAll -> selectAll(previousState)
                is EncryptionAction.SetFavorite -> setFavorite(action.encryptionIds)
                is EncryptionAction.UnsetFavorite -> unsetFavorite(action.encryptionIds)
                is EncryptionAction.AskAboutIncomingData -> askAboutIncomingData(action.data)
                is EncryptionAction.ResolveIncomingData -> resolveIncomingData(action.encryptionId)
            }
        }.onFailure {
            Napier.e(tag = tag, message = "processAction: $action", throwable = it)
        }
    }

    private suspend fun collectEncryptions() {
        collectJob?.cancel()
        collectJob = launch {
            repository.observe().cancellable().collect {
                emitNewState(
                    currentState.copy(
                        initialized = true,
                        encryptions = it.sorted()
                    )
                )
            }
        }
    }

    private suspend fun createEncryption(action: EncryptionAction.Create) {
        val newEncryption =
            repository.create(name = action.name, password = action.password, algorithm = action.algorithm)
        emitSideEffect(EncryptionEffect.Created(newEncryption))
    }

    private suspend fun editEncryption(action: EncryptionAction.Edit) {
        val editedEncryption = repository.edit(
            encryptionToEdit = action.encryptionToEdit,
            name = action.name,
            password = action.password,
            algorithm = action.algorithm
        )
        emitSideEffect(EncryptionEffect.Edited(editedEncryption))
    }

    private suspend fun removeEncryption(ids: Set<String>) {
        repository.delete(ids)
        emitSideEffect(EncryptionEffect.Removed(ids))
    }

    private fun selectEncryption(previousState: EncryptionState, encryptionId: String) {
        emitNewState(
            previousState.copy(
                selectedEncryptionIds = previousState.selectedEncryptionIds.toMutableSet().apply {
                    add(encryptionId)
                }
            )
        )
    }

    private fun unselectEncryption(previousState: EncryptionState, encryptionId: String) {
        emitNewState(
            previousState.copy(
                selectedEncryptionIds = previousState.selectedEncryptionIds.toMutableSet().apply {
                    remove(encryptionId)
                }
            )
        )
    }

    private fun unselectAll(previousState: EncryptionState) {
        emitNewState(
            previousState.copy(
                selectedEncryptionIds = emptySet()
            )
        )
    }

    private fun selectAll(previousState: EncryptionState) {
        emitNewState(
            previousState.copy(
                selectedEncryptionIds = previousState.encryptions.map { it.id }.toSet()
            )
        )
    }

    private suspend fun setFavorite(ids: Set<String>) {
        repository.setFavorite(ids)
        emitSideEffect(EncryptionEffect.SetFavorite(ids))
    }

    private suspend fun unsetFavorite(ids: Set<String>) {
        repository.unsetFavorite(ids)
        emitSideEffect(EncryptionEffect.UnsetFavorite(ids))
    }

    private fun askAboutIncomingData(data: String) {
        incomingData = data
        emitNewState(currentState.copy(incomingData = data))
    }

    private suspend fun resolveIncomingData(encryptionId: String?) {
        val data = incomingData
        if (data != null && encryptionId != null) {
            runCatching {
                messageReceiver.receive(encryptionId = encryptionId, encryptedMessage = data, isRead = false)
            }.onFailure {
                emitSideEffect(
                    EncryptionEffect.Error(
                        localizedError.messageReceiveMessage,
                        retry = EncryptionAction.AskAboutIncomingData(data),
                    )
                )
            }
        }
        incomingData = null
        emitNewState(currentState.copy(incomingData = null))
    }

    private fun Collection<Encryption>.sorted(): List<Encryption> {
        return sortedWith(compareBy({ !it.isFavorite }, { -it.lastMessageTimestamp }))
    }

}