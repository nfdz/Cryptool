package io.github.nfdz.cryptool.shared.platform.cryptography

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion

actual class CryptographyV1 actual constructor() : Cryptography {
    override val version: AlgorithmVersion
        get() = TODO("Not yet implemented")

    override suspend fun encrypt(password: String, text: String): String? {
        TODO("Not yet implemented")
    }

    override suspend fun decrypt(password: String, encryptedText: String): String? {
        TODO("Not yet implemented")
    }
}

actual class CryptographyV2 actual constructor() : Cryptography {
    override val version: AlgorithmVersion
        get() = TODO("Not yet implemented")

    override suspend fun encrypt(password: String, text: String): String? {
        TODO("Not yet implemented")
    }

    override suspend fun decrypt(password: String, encryptedText: String): String? {
        TODO("Not yet implemented")
    }
}