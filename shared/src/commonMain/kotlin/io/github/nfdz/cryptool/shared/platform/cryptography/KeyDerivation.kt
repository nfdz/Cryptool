package io.github.nfdz.cryptool.shared.platform.cryptography

interface KeyDerivation {
    suspend fun hash(password: String, salt: ByteArray, hashLength: Int): ByteArray
    suspend fun generateSalt(): ByteArray
}

expect class Argon2KeyDerivation() : KeyDerivation