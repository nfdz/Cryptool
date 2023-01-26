package io.github.nfdz.cryptool.shared.encryption.entity

sealed class MessageSource(val exclusive: Boolean) {
    object Manual : MessageSource(false)
    data class Sms(val phone: String) : MessageSource(true)
    data class File(val inputFilePath: String, val outputFilePath: String) : MessageSource(true)
}

fun MessageSource.serialize(): String {
    return when (this) {
        MessageSource.Manual -> manualName
        is MessageSource.Sms -> "$smsPrefix$phone"
        is MessageSource.File -> "$filePrefix$inputFilePath+$outputFilePath"
    }
}

fun String.deserializeMessageSource(): MessageSource {
    return when {
        this == manualName -> MessageSource.Manual
        this.startsWith(smsPrefix) -> MessageSource.Sms(this.removePrefix(smsPrefix))
        this.startsWith(filePrefix) -> {
            val parts = this.removePrefix(filePrefix).split("+")
            MessageSource.File(inputFilePath = parts[0], outputFilePath = parts[1])
        }
        else -> throw IllegalArgumentException("Cannot deserialize message source: $this")
    }
}

private const val manualName = "MANUAL"
private const val smsPrefix = "SMS:"
private const val filePrefix = "FILE:"
