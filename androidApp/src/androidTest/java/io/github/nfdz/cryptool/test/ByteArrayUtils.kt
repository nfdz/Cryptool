package io.github.nfdz.cryptool.test

fun ByteArray.encodeHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
fun String.decodeHex(): ByteArray {
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}