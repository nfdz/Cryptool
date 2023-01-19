package io.github.nfdz.cryptool.shared.platform.cryptography

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.signal.argon2.Argon2
import org.signal.argon2.MemoryCost
import org.signal.argon2.Type
import org.signal.argon2.Version
import java.security.SecureRandom

actual class Argon2KeyDerivation actual constructor() : KeyDerivation {

    private val random by lazy { SecureRandom() }

    override suspend fun hash(password: String, salt: ByteArray, hashLength: Int): ByteArray =
        withContext(Dispatchers.Default) {
            Argon2.Builder(Version.V13)
                .type(Type.Argon2id)
                .hashLength(hashLength)
                .memoryCost(MemoryCost.MiB(32))
                .parallelism(1)
                .iterations(3)
                .build()
                .hash(password.toByteArray(), salt).hash
        }

    override suspend fun generateSalt(): ByteArray = withContext(Dispatchers.Default) {
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        bytes
    }

}