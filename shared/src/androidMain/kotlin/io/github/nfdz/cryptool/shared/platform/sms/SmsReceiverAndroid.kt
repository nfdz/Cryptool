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
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SmsReceiverAndroid(
    private val context: Context,
    private val encryptionRepository: EncryptionRepository,
    private val messageRepository: MessageRepository,
    private val keyValueStorage: KeyValueStorage,
) : SmsReceiver, CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        private const val tag = "SmsReceiver"
        private const val lastCheckTimestampMillisKey = "sms_last_check_timestamp"
        private val inbox = Uri.parse("content://sms/inbox")
        private val projection = arrayOf(Sms.DATE, Sms.ADDRESS, Sms.BODY)
        private val dateIndex = 0
        private val addressIndex = 1
        private val bodyIndex = 2
    }

    private val telephonyManager
        get() = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    override fun receivePendingMessage() {
        if (context.hasPermission(smsPermissions)) {
            launch {
                runCatching {
                    getPendingFromContentResolver().forEach { data ->
                        receiveMessageInternal(data)
                    }
                    keyValueStorage.putLong(lastCheckTimestampMillisKey, System.currentTimeMillis())
                }.onFailure {
                    Napier.e(tag = tag, message = "Error receiving pending SMSs", throwable = it)
                }
            }
        }
    }

    private fun getPendingFromContentResolver(): List<MessageToReceive> {
        val result = mutableListOf<MessageToReceive>()
        val lastCheckTimestampMillis = keyValueStorage.getLong(lastCheckTimestampMillisKey, 0)
        val selection = "${Sms.DATE} > '$lastCheckTimestampMillis'"
        context.contentResolver.query(inbox, projection, selection, null, null)?.use {
            if (it.moveToFirst()) {
                do {
                    val timestampInMillis = it.getString(dateIndex).toLong()
                    if (timestampInMillis > lastCheckTimestampMillis) {
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
        return result
    }

    private suspend fun receiveMessageInternal(data: MessageToReceive) {
        Napier.d(tag = tag, message = "Message from ${data.phone}: '${data.encryptedMessage}'")
        val encryption = findEncryption(data.phone)
            ?: return Napier.d(tag = tag, message = "Cannot find an encryption for this message")
        val cryptography = encryption.algorithm.createCryptography()
        val message = cryptography.decrypt(encryption.password, data.encryptedMessage)
            ?: return Napier.d(tag = tag, message = "Cannot process the message")
        messageRepository.receiveMessageAsync(
            encryptionId = encryption.id,
            message = message,
            encryptedMessage = data.encryptedMessage,
            timestampInMillis = data.timestampInMillis,
        )
    }

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