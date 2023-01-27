package io.github.nfdz.cryptool.shared.encryption.entity

sealed class MessageSource(val exclusive: Boolean) {
    companion object {
        const val manualName = "MANUAL"
        const val smsPrefix = "SMS:"
        const val filePrefix = "FILE:"
    }

    object Manual : MessageSource(false)
    data class Sms(val phone: String) : MessageSource(true)
    data class File(val inputFilePath: String, val outputFilePath: String) : MessageSource(true)
}

fun MessageSource.serialize(): String {
    return when (this) {
        MessageSource.Manual -> MessageSource.manualName
        is MessageSource.Sms -> "${MessageSource.smsPrefix}$phone"
        is MessageSource.File -> "${MessageSource.filePrefix}$inputFilePath+$outputFilePath"
    }
}

fun String.deserializeMessageSource(): MessageSource {
    return when {
        this == MessageSource.manualName -> MessageSource.Manual
        this.startsWith(MessageSource.smsPrefix) -> MessageSource.Sms(this.removePrefix(MessageSource.smsPrefix))
        this.startsWith(MessageSource.filePrefix) -> {
            val parts = this.removePrefix(MessageSource.filePrefix).split("+")
            MessageSource.File(inputFilePath = parts[0], outputFilePath = parts[1])
        }
        else -> throw IllegalArgumentException("Cannot deserialize message source: $this")
    }
}
