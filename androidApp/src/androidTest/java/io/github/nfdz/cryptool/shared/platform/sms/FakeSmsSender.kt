package io.github.nfdz.cryptool.shared.platform.sms

class FakeSmsSender(
    private val sendMessageException: Throwable? = null
) : SmsSender {

    var sendMessageCount = 0
    var sendMessageArgPhone: String? = null
    var sendMessageArgValue: String? = null
    override fun sendMessage(phone: String, value: String) {
        sendMessageCount++
        sendMessageArgPhone = phone
        sendMessageArgValue = value
        sendMessageException?.let { throw it }
    }

}