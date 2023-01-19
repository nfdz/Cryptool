package io.github.nfdz.cryptool.shared.platform.cryptography

expect fun ByteArray.decompressGzip(): String
expect fun String.compressGzip(): ByteArray