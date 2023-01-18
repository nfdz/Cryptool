package io.github.nfdz.cryptool.shared.platform.cryptography

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion

interface Cryptography {
    val version: AlgorithmVersion
    suspend fun encrypt(password: String, text: String): String?
    suspend fun decrypt(password: String, encryptedText: String): String?
}

expect class CryptographyV1() : Cryptography
expect class CryptographyV2() : Cryptography