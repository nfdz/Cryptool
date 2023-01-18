package io.github.nfdz.cryptool.shared.platform.cryptography

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

actual fun ByteArray.decompressGzip(): String {
    return GZIPInputStream(this.inputStream()).bufferedReader(Charsets.UTF_8).use { it.readText() }
}

actual fun String.compressGzip(): ByteArray {
    val bos = ByteArrayOutputStream()
    GZIPOutputStream(bos).bufferedWriter(Charsets.UTF_8).use { it.write(this) }
    return bos.toByteArray()
}
