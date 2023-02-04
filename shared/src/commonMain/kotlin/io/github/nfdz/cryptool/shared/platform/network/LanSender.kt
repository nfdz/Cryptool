package io.github.nfdz.cryptool.shared.platform.network

import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource

interface LanSender {
    fun sendMessage(lanSource: MessageSource.Lan, encryptedMessage: String)
}

class LanSendException(cause: Throwable) : Exception(cause)
