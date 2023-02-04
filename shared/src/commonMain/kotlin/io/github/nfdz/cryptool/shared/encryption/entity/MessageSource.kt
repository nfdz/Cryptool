package io.github.nfdz.cryptool.shared.encryption.entity

sealed class MessageSource(val exclusive: Boolean) {
    companion object {
        const val manualName = "MANUAL"
        const val smsPrefix = "SMS:"
        const val filePrefix = "FILE:"
        const val lanPrefix = "LAN:"
    }

    object Manual : MessageSource(false)
    data class Sms(val phone: String) : MessageSource(true)
    data class File(val inputFilePath: String, val outputFilePath: String) : MessageSource(true)
    data class Lan(val address: String, val port: String, val slot: String) : MessageSource(true)
}

fun MessageSource.serialize(): String {
    return when (this) {
        MessageSource.Manual -> MessageSource.manualName
        is MessageSource.Sms -> "${MessageSource.smsPrefix}$phone"
        is MessageSource.File -> "${MessageSource.filePrefix}$inputFilePath+$outputFilePath"
        is MessageSource.Lan -> "${MessageSource.lanPrefix}$address+$port+$slot"
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
        this.startsWith(MessageSource.lanPrefix) -> {
            val parts = this.removePrefix(MessageSource.lanPrefix).split("+")
            MessageSource.Lan(address = parts[0], port = parts[1], slot = parts[2])
        }
        else -> throw IllegalArgumentException("Cannot deserialize message source: $this")
    }
}
