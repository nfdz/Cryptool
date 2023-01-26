package io.github.nfdz.cryptool.shared.message.viewModel

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.encryption.repository.ExclusiveSourceCollisionException
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.platform.localization.LocalizedError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

class MessageViewModelImpl(
    private val messageRepository: MessageRepository,
    private val encryptionRepository: EncryptionRepository,
    private val localizedError: LocalizedError,
) : MessageViewModelBase() {

    override val tag: String
        get() = "MessageViewModel"

    override val initialState: MessageState
        get() = MessageState.empty

    private var collectMessagesJob: Job? = null
    private var collectEncryptionJob: Job? = null

    override suspend fun processAction(action: MessageAction) {
        val previousState = currentState
        runCatching {
            when (action) {
                is MessageAction.Initialize -> collectMessages(action.encryptionId)
                MessageAction.Close -> close(previousState)
                is MessageAction.AcknowledgeUnreadMessages -> acknowledgeUnreadMessages(action.encryptionId)
                is MessageAction.SetSource -> setSource(action.source)
                is MessageAction.ReceiveMessage -> receiveMessage(action.encryptedMessage)
                is MessageAction.SendMessage -> sendMessage(action.message)
                is MessageAction.Remove -> removeMessage(action.messageIds)
                is MessageAction.Select -> selectMessage(previousState, action.messageId)
                is MessageAction.Unselect -> unselectMessage(previousState, action.messageId)
                MessageAction.UnselectAll -> unselectAll(previousState)
                MessageAction.SelectAll -> selectAll(previousState)
                is MessageAction.SetFavorite -> setFavorite(action.messageIds)
                is MessageAction.UnsetFavorite -> unsetFavorite(action.messageIds)
                MessageAction.ToggleVisibility -> toggleVisibility(previousState)
                is MessageAction.Event -> processEvent(action.message)
            }
        }.onFailure {
            Napier.e(tag = tag, message = "processAction: $action", throwable = it)
            emitSideEffect(MessageEffect.Error(localizedError.messageUnexpected))
        }
    }

    private suspend fun acknowledgeUnreadMessages(encryptionId: String) {
        encryptionRepository.acknowledgeUnreadMessages(encryptionId)
    }

    private suspend fun collectMessages(encryptionId: String) {
        collectMessagesJob?.cancel()
        collectEncryptionJob?.cancel()

        emitNewState(MessageState.empty.copy(visibility = messageRepository.getVisibilityPreference()))

        collectEncryptionJob = launch {
            encryptionRepository.observe(encryptionId).cancellable().collect {
                emitNewState(
                    currentState.copy(
                        encryption = it,
                    )
                )
            }
        }
        collectMessagesJob = launch {
            messageRepository.observe(encryptionId).cancellable().collect {
                emitNewState(
                    currentState.copy(
                        messages = it.sorted(),
                    )
                )
            }
        }
    }

    private suspend fun close(previousState: MessageState) {
        collectMessagesJob?.cancel()
        collectEncryptionJob?.cancel()
        collectMessagesJob = null
        collectEncryptionJob = null
        previousState.encryption?.id?.let {
            encryptionRepository.acknowledgeUnreadMessages(it)
        }
        emitNewState(MessageState.empty)
    }

    private val activeEncryption: Encryption
        get() = currentState.encryption ?: throw IllegalStateException("No encryption")

    private suspend fun setSource(source: MessageSource?) {
        try {
            encryptionRepository.setSource(id = activeEncryption.id, source = source)
            emitSideEffect(MessageEffect.SetSource(source))
        } catch (collision: ExclusiveSourceCollisionException) {
            emitSideEffect(MessageEffect.Error(localizedError.exclusiveSourceCollision))
        }
    }

    private suspend fun receiveMessage(encryptedMessage: String) {
        runCatching {
            messageRepository.receiveMessage(encryptionId = activeEncryption.id, encryptedMessage = encryptedMessage)
            emitSideEffect(MessageEffect.ReceivedMessage)
        }.onFailure {
            emitSideEffect(MessageEffect.Error(localizedError.messageReceiveMessage))
        }
    }

    private suspend fun sendMessage(message: String) {
        messageRepository.sendMessage(encryptionId = activeEncryption.id, message = message)
        emitSideEffect(MessageEffect.SentMessage)
    }

    private suspend fun removeMessage(messageIds: Set<String>) {
        messageRepository.delete(messageIds)
        emitSideEffect(MessageEffect.RemovedMessage)
    }

    private fun selectMessage(previousState: MessageState, messageId: String) {
        emitNewState(previousState.copy(selectedMessageIds = previousState.selectedMessageIds.toMutableSet().apply {
            add(messageId)
        }))
    }

    private fun unselectMessage(previousState: MessageState, messageId: String) {
        emitNewState(previousState.copy(selectedMessageIds = previousState.selectedMessageIds.toMutableSet().apply {
            remove(messageId)
        }))
    }

    private fun unselectAll(previousState: MessageState) {
        emitNewState(
            previousState.copy(
                selectedMessageIds = emptySet()
            )
        )
    }

    private fun selectAll(previousState: MessageState) {
        val all = previousState.messages.filter { it.ownership != MessageOwnership.SYSTEM }
            .map { it.id }.toSet()
        emitNewState(
            previousState.copy(
                selectedMessageIds = all
            )
        )
    }

    private suspend fun setFavorite(messageIds: Set<String>) {
        messageRepository.setFavorite(messageIds)
        emitSideEffect(MessageEffect.SetFavorite)
    }

    private suspend fun unsetFavorite(messageIds: Set<String>) {
        messageRepository.unsetFavorite(messageIds)
        emitSideEffect(MessageEffect.UnsetFavorite)
    }

    private suspend fun toggleVisibility(previousState: MessageState) {
        val visibility = !previousState.visibility
        messageRepository.setVisibilityPreference(visibility)
        emitNewState(
            previousState.copy(
                visibility = visibility
            )
        )
    }

    private suspend fun processEvent(message: String) {
        emitSideEffect(MessageEffect.Event(message))
    }

    private fun Collection<Message>.sorted(): List<Message> {
        return sortedBy { -it.timestampInMillis }
    }
}