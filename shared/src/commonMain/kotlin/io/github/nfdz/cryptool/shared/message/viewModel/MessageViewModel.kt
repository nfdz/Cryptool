package io.github.nfdz.cryptool.shared.message.viewModel

import io.github.nfdz.cryptool.shared.core.viewModel.Action
import io.github.nfdz.cryptool.shared.core.viewModel.Effect
import io.github.nfdz.cryptool.shared.core.viewModel.NanoViewModel
import io.github.nfdz.cryptool.shared.core.viewModel.NanoViewModelBase
import io.github.nfdz.cryptool.shared.core.viewModel.State
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.entity.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class MessageState(
    val encryption: Encryption?,
    val messages: List<Message>,
    val selectedMessageIds: Set<String>,
    val searchResultMessageIds: Set<String>,
    val searchText: String?,
    val visibility: Boolean,
) : State {
    companion object {
        val empty = MessageState(null, emptyList(), emptySet(), emptySet(), null, true)
    }

    override fun toString(): String {
        return "$MessageState(encryptionId=${encryption?.id}, messages.size=${messages.size}, selectedMessageIds.size=${selectedMessageIds.size}, visibility=$visibility)"
    }
}

sealed class MessageAction : Action {
    data class Initialize(val encryptionId: String) : MessageAction()
    data object Close : MessageAction()
    data class AcknowledgeUnreadMessages(val encryptionId: String) : MessageAction()
    data class SetSource(val source: MessageSource?) : MessageAction()
    data class ReceiveMessage(val encryptedMessage: String) : MessageAction()
    data class SendMessage(val message: String) : MessageAction()
    data class RetrySendMessage(val encryptionId: String, val message: String) : MessageAction()
    data class Remove(val messageIds: Set<String>) : MessageAction()
    data class SetFavorite(val messageIds: Set<String>) : MessageAction()
    data class UnsetFavorite(val messageIds: Set<String>) : MessageAction()
    data class Select(val messageId: String) : MessageAction()
    data class Unselect(val messageId: String) : MessageAction()
    data object ToggleVisibility : MessageAction()
    data object UnselectAll : MessageAction()
    data object SelectAll : MessageAction()
    data class Search(val text: String?) : MessageAction()
    data class Event(val message: String) : MessageAction()

}

sealed class MessageEffect : Effect {
    data class SetSource(val source: MessageSource?) : MessageEffect()
    data object ReceivedMessage : MessageEffect()
    data object SentMessage : MessageEffect()
    data object RemovedMessage : MessageEffect()
    data object SetFavorite : MessageEffect()
    data object UnsetFavorite : MessageEffect()
    class Error(val message: String, val retry: MessageAction? = null) : MessageEffect()
    class Event(val message: String) : MessageEffect()
}

interface MessageViewModel : NanoViewModel<MessageState, MessageAction, MessageEffect>
abstract class MessageViewModelBase : MessageViewModel, NanoViewModelBase<MessageState, MessageAction, MessageEffect>()

object EmptyMessageViewModel : MessageViewModel {
    override fun observeState(): StateFlow<MessageState> = throw IllegalStateException()
    override fun observeSideEffect(): Flow<MessageEffect> = throw IllegalStateException()
    override fun dispatch(action: MessageAction) {}
}
