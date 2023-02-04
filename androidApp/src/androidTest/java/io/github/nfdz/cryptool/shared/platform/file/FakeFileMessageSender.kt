package io.github.nfdz.cryptool.shared.platform.file

class FakeFileMessageSender(
    private val sendMessageException: Throwable? = null
) : FileMessageSender {

    var sendMessageCount = 0
    var sendMessageArgPath: String? = null
    var sendMessageArgValue: String? = null
    override fun sendMessage(outputFilePath: String, encryptedMessage: String) {
        sendMessageCount++
        sendMessageArgPath = outputFilePath
        sendMessageArgValue = encryptedMessage
        sendMessageException?.let { throw it }
    }

}
