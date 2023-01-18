package io.github.nfdz.cryptool.shared.encryption.entity

import io.github.nfdz.cryptool.shared.platform.cryptography.Cryptography
import io.github.nfdz.cryptool.shared.platform.cryptography.CryptographyV1
import io.github.nfdz.cryptool.shared.platform.cryptography.CryptographyV2

enum class AlgorithmVersion(val description: String) {
    V1("AES-CBC PBKDF2 (unsafe)"),
    V2("AES256-GCM_ARGON2");

    fun createCryptography(): Cryptography = when (this) {
        V1 -> CryptographyV1()
        V2 -> CryptographyV2()
    }
}