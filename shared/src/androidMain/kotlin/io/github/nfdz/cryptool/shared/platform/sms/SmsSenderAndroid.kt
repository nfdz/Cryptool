package io.github.nfdz.cryptool.shared.platform.sms

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import kotlin.random.Random

class SmsSenderAndroid(
    private val context: Context,
    messageRepository: MessageRepository
) : SmsSender {

    companion object {
        private const val tag = "SmsSender"
        const val smsDeliveredAction = "io.github.nfdz.cryptool.SMS_DELIVERED"
        const val smsSentAction = "io.github.nfdz.cryptool.SMS_SENT"
    }

    private val smsManager
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(android.telephony.SmsManager::class.java)
        } else {
            android.telephony.SmsManager.getDefault()
        }

    init {
        messageRepository.addOnSendMessageAction { source, encryptedMessage ->
            val smsSource = source as? MessageSource.Sms ?: return@addOnSendMessageAction
            sendMessage(smsSource.phone, encryptedMessage)
        }
    }

    override fun sendMessage(phone: String, encryptedMessage: String) {
        Napier.d(tag = tag, message = "Send message to $phone: '$encryptedMessage'")
        smsManager.sendTextMessage(
            phone,
            null,
            encryptedMessage,
            createPendingIntent(smsSentAction),
            createPendingIntent(smsDeliveredAction)
        )
    }

    private fun createPendingIntent(action: String): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            randomRequestCode(),
            Intent(action).setPackage(context.packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun randomRequestCode(): Int = Random.nextInt(100, 10000)

}