package io.github.nfdz.cryptool.shared.platform.file

interface FileMessageSender {
    fun sendMessage(outputFilePath: String, value: String)
}
class FileMessageSendException(cause: Throwable) : Exception(cause)
