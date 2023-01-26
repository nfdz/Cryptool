package io.github.nfdz.cryptool.platform.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.message.viewModel.MessageAction
import io.github.nfdz.cryptool.shared.message.viewModel.MessageViewModel
import io.github.nfdz.cryptool.shared.platform.sms.SmsSenderAndroid

object MessageEventBroadcast {

    private val eventActions = listOf(
        SmsSenderAndroid.smsSentAction,
        SmsSenderAndroid.smsDeliveredAction,
    )



    fun createReceiver(messageViewModel: MessageViewModel): MessageEventBroadcastReceiver =
        MessageEventBroadcastReceiver(messageViewModel)

    fun registerReceiver(context: Context, receiver: MessageEventBroadcastReceiver) {
        context.registerReceiver(receiver, IntentFilter().apply {
            eventActions.forEach {
                addAction(it)
            }
        })
    }

    fun unregisterReceiver(context: Context, receiver: MessageEventBroadcastReceiver) {
        context.unregisterReceiver(receiver)
    }

}

class MessageEventBroadcastReceiver(private val messageViewModel: MessageViewModel) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Napier.d(tag = "MessageEventBroadcastReceiver", message = "Received event: ${intent?.action}")
        val message = actionToMessage(intent?.action) ?: return
        messageViewModel.dispatch(MessageAction.Event(message))
    }

    private fun actionToMessage(action: String?): String? {
        return when (action) {
            SmsSenderAndroid.smsSentAction -> "TODO smsSentAction"
            SmsSenderAndroid.smsDeliveredAction -> "TODO smsDeliveredAction"
            else -> {
                Napier.e(tag = "MessageEventBroadcastReceiver", message = "Unknown received event: $action")
                null
            }
        }
    }

}