package io.github.nfdz.cryptool.shared.platform.cryptography

expect fun ByteArray.encodeBase64(): String
expect fun String.decodeBase64(): ByteArray