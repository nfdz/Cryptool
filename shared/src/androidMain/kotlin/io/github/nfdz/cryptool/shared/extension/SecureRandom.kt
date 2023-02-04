package io.github.nfdz.cryptool.shared.extension

import java.security.SecureRandom

fun SecureRandom.generateIV(blockSize: Int): ByteArray {
    val iv = ByteArray(blockSize)
    nextBytes(iv)
    return iv
}