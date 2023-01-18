package io.github.nfdz.cryptool.shared.platform.cryptography

actual class Argon2KeyDerivation actual constructor() : KeyDerivation {
    override suspend fun hash(password: String, salt: ByteArray, hashLength: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun generateSalt(): ByteArray {
        TODO("Not yet implemented")
    }
}