package io.github.nfdz.cryptool.platform.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.R
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
        context ?: return
        if (context.packageName != intent?.`package`) return
        Napier.d(tag = "MessageEventBroadcastReceiver", message = "Received event: ${intent?.action}")
        val message = actionToMessage(context, intent?.action) ?: return
        messageViewModel.dispatch(MessageAction.Event(message))
    }

    private fun actionToMessage(context: Context, action: String?): String? {
        return when (action) {
            SmsSenderAndroid.smsSentAction -> context.getString(R.string.encryption_sms_sent_snackbar)
            SmsSenderAndroid.smsDeliveredAction -> context.getString(R.string.encryption_sms_delivered_snackbar)
            else -> {
                Napier.e(tag = "MessageEventBroadcastReceiver", message = "Unknown received event: $action")
                assert(false)
                null
            }
        }
    }

}