package io.github.nfdz.cryptool.shared.platform.file

interface FileMessageSender {
    fun sendMessage(outputFilePath: String, encryptedMessage: String)
}
class FileMessageSendException(cause: Throwable) : Exception(cause)
