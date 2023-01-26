package io.github.nfdz.cryptool.platform.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import io.github.nfdz.cryptool.shared.platform.sms.SmsReceiver
import org.koin.core.context.GlobalContext

class SmsBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val tag = "SmsBroadcastReceiver"
    }

    private val gatekeeperRepository: GatekeeperRepository by lazy { GlobalContext.get().get() }
    private val smsReceiver: SmsReceiver by lazy { GlobalContext.get().get() }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> onReceiveSms()
            else -> Napier.e(tag = tag, message = "Received unexpected action: ${intent?.action}")
        }
    }

    private fun onReceiveSms() {
        if (!gatekeeperRepository.isOpen()) {
            Napier.d(tag = tag, message = "Cryptool is not open")
            return
        }
        smsReceiver.receivePendingMessage()
    }


}