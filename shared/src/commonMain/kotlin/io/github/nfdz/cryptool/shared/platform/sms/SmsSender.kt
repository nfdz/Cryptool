package io.github.nfdz.cryptool.shared.platform.sms

interface SmsSender {
    fun sendMessage(phone: String, value: String)
}