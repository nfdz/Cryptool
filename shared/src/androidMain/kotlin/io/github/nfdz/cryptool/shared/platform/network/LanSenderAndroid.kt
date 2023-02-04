package io.github.nfdz.cryptool.shared.platform.network

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import java.net.InetSocketAddress
import java.net.Socket

class LanSenderAndroid(
    messageRepository: MessageRepository
) : LanSender {

    companion object {
        private const val tag = "LanSender"
        private const val sendTimeoutInMillis = 4_000
    }

    init {
        messageRepository.addOnSendMessageAction { source, encryptedMessage ->
            val lanSource = source as? MessageSource.Lan ?: return@addOnSendMessageAction
            sendMessage(lanSource, encryptedMessage)
        }
    }

    override fun sendMessage(lanSource: MessageSource.Lan, encryptedMessage: String) {
        Napier.d(tag = tag, message = "Send message to $lanSource: '$encryptedMessage'")
        runCatching {
            Socket().use { client ->
                client.connect(InetSocketAddress(lanSource.address, lanSource.port.toInt()), sendTimeoutInMillis)
                client.getOutputStream().bufferedWriter(Charsets.UTF_8).use {
                    it.write("${lanSource.slot},$encryptedMessage")
                }
            }
        }.onFailure {
            Napier.e(tag = tag, message = "Send message error", throwable = it)
            throw LanSendException(it)
        }
    }

}