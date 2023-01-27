package io.github.nfdz.cryptool.shared.message.viewModel

import io.github.nfdz.cryptool.shared.core.viewModel.*
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.entity.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class MessageState(
    val encryption: Encryption?,
    val messages: List<Message>,
    val selectedMessageIds: Set<String>,
    val visibility: Boolean,
) : State {
    companion object {
        val empty = MessageState(null, emptyList(), emptySet(), true)
    }

    override fun toString(): String {
        return "$MessageState(encryptionId=${encryption?.id}, messages.size=${messages.size}, selectedMessageIds.size=${selectedMessageIds.size}, visibility=$visibility)"
    }
}

sealed class MessageAction : Action {
    data class Initialize(val encryptionId: String) : MessageAction()
    object Close : MessageAction()
    data class AcknowledgeUnreadMessages(val encryptionId: String) : MessageAction()
    data class SetSource(val source: MessageSource?) : MessageAction()
    data class ReceiveMessage(val encryptedMessage: String) : MessageAction()
    data class SendMessage(val message: String) : MessageAction()
    data class Remove(val messageIds: Set<String>) : MessageAction()
    data class SetFavorite(val messageIds: Set<String>) : MessageAction()
    data class UnsetFavorite(val messageIds: Set<String>) : MessageAction()
    data class Select(val messageId: String) : MessageAction()
    data class Unselect(val messageId: String) : MessageAction()
    object ToggleVisibility : MessageAction()
    object UnselectAll : MessageAction()
    object SelectAll : MessageAction()
    data class Event(val message: String) : MessageAction()
}

sealed class MessageEffect : Effect {
    data class SetSource(val source: MessageSource?) : MessageEffect()
    object ReceivedMessage : MessageEffect()
    object SentMessage : MessageEffect()
    object RemovedMessage : MessageEffect()
    object SetFavorite : MessageEffect()
    object UnsetFavorite : MessageEffect()
    class Error(val message: String) : MessageEffect()
    class Event(val message: String) : MessageEffect()
}

interface MessageViewModel : NanoViewModel<MessageState, MessageAction, MessageEffect>
abstract class MessageViewModelBase : MessageViewModel, NanoViewModelBase<MessageState, MessageAction, MessageEffect>()

object EmptyMessageViewModel : MessageViewModel {
    override fun observeState(): StateFlow<MessageState> = throw IllegalStateException()
    override fun observeSideEffect(): Flow<MessageEffect> = throw IllegalStateException()
    override fun dispatch(action: MessageAction) {}
}
