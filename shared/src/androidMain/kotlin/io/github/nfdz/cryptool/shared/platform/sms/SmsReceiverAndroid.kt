package io.github.nfdz.cryptool.shared.platform.sms

import android.content.Context
import android.net.Uri
import android.provider.Telephony.Sms
import android.telephony.TelephonyManager
import com.google.i18n.phonenumbers.PhoneNumberUtil
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.extension.hasPermission
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.github.nfdz.cryptool.shared.platform.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiverAndroid(
    private val context: Context,
    private val encryptionRepository: EncryptionRepository,
    gatekeeperRepository: GatekeeperRepository,
    private val messageRepository: MessageRepository,
    private val storage: KeyValueStorage,
) : SmsReceiver, CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        private const val tag = "SmsReceiver"
        private val inbox = Uri.parse("content://sms/inbox")
        private val projection = arrayOf(Sms.DATE, Sms.ADDRESS, Sms.BODY)
        private const val dateIndex = 0
        private const val addressIndex = 1
        private const val bodyIndex = 2
    }

    private val telephonyManager
        get() = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    init {
        gatekeeperRepository.addOnOpenAction(::receivePendingMessage)
        gatekeeperRepository.addOnResetAction(::afterReset)
        encryptionRepository.addOnSetSourceAction { source ->
            if (source is MessageSource.Sms) {
                SmsReceiverPreferences.setLastReceivedTimestamp(
                    storage,
                    Clock.nowInMillis()
                )
            }
        }
    }

    override fun receivePendingMessage() {
        if (!context.hasPermission(smsPermissions)) {
            Napier.e(tag = tag, message = "Do not have permission")
            return
        }
        Napier.d(tag = tag, message = "Receive pending messages")
        launch {
            val result = getPendingFromContentResolver().sortedBy { it.timestampInMillis }
            if (result.isNotEmpty()) {
                result.forEach { data ->
                    runCatching {
                        val encryption = findEncryption(data.phone)
                            ?: throw IllegalStateException("Cannot find an encryption for this message")
                        messageRepository.receiveMessageAsync(
                            encryption = encryption,
                            encryptedMessage = data.encryptedMessage,
                            timestampInMillis = data.timestampInMillis,
                        )
                    }.onFailure {
                        Napier.e(tag = tag, message = "Error processing pending SMS", throwable = it)
                    }
                }
                SmsReceiverPreferences.setLastReceivedTimestamp(storage, result.last().timestampInMillis)
            }
        }
    }

    private fun afterReset() {
        SmsReceiverPreferences.setBaseline(storage)
    }

    private fun getPendingFromContentResolver(): List<MessageToReceive> = runCatching {
        val result = mutableListOf<MessageToReceive>()
        val lastReceivedTimestampMillis = SmsReceiverPreferences.getLastReceivedTimestamp(storage)
        val selection = "${Sms.DATE} > '${lastReceivedTimestampMillis - 1000L}'"
        context.contentResolver.query(inbox, projection, selection, null, null)?.use {
            if (it.moveToFirst()) {
                do {
                    val timestampInMillis = it.getString(dateIndex).toLong()
                    if (timestampInMillis > lastReceivedTimestampMillis) {
                        val phone = it.getString(addressIndex)
                        val encryptedMessage = it.getString(bodyIndex)
                        result.add(
                            MessageToReceive(
                                phone = phone,
                                encryptedMessage = encryptedMessage,
                                timestampInMillis = timestampInMillis,
                            )
                        )
                    }
                } while (it.moveToNext())
            }
        }
        result
    }.onFailure {
        Napier.e(tag = tag, message = "Error receiving pending SMSs", throwable = it)
    }.getOrElse { emptyList() }

    private fun findEncryption(phone: String): Encryption? {
        val hasCountryCode = phone.contains('+')
        return if (hasCountryCode) {
            encryptionRepository.getWithPhone(phone) ?: runCatching {
                val countryCode = telephonyManager.simCountryIso.uppercase()
                val countryPrefix = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryCode)
                encryptionRepository.getWithPhone(phone.removePrefix("+$countryPrefix"))
            }.getOrNull()
        } else {
            encryptionRepository.getWithPhone(phone) ?: runCatching {
                val countryCode = telephonyManager.simCountryIso.uppercase()
                val countryPrefix = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryCode)
                encryptionRepository.getWithPhone("+$countryPrefix$phone")
            }.getOrNull()
        }
    }

    private fun EncryptionRepository.getWithPhone(phone: String): Encryption? {
        val encryptions = getAllWith(MessageSource.Sms(phone))
        assert(encryptions.size <= 1)
        return encryptions.firstOrNull()
    }

}

private class MessageToReceive(val phone: String, val encryptedMessage: String, val timestampInMillis: Long)