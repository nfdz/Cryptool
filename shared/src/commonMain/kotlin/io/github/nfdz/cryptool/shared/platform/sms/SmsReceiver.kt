package io.github.nfdz.cryptool.shared.platform.sms

interface SmsReceiver {
    fun receivePendingMessage()
    fun afterReset()
}
