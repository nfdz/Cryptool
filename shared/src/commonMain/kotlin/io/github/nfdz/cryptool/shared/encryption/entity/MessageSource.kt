package io.github.nfdz.cryptool.shared.encryption.entity

sealed class MessageSource(val exclusive: Boolean) {
    object Manual : MessageSource(false)
    data class Sms(val phone: String) : MessageSource(true)
}

fun MessageSource.serialize(): String {
    return when (this) {
        MessageSource.Manual -> "MANUAL"
        is MessageSource.Sms -> "SMS:$phone"
    }
}

fun String.deserializeMessageSource(): MessageSource {
    return when {
        this == "MANUAL" -> MessageSource.Manual
        this.startsWith("SMS:") -> MessageSource.Sms(this.split(":")[1])
        else -> throw IllegalArgumentException("Cannot deserialize message source: $this")
    }
}
