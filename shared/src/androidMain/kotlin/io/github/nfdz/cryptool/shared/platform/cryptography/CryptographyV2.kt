package io.github.nfdz.cryptool.shared.platform.cryptography

import android.security.keystore.KeyProperties
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

actual class CryptographyV2 actual constructor() : Cryptography {
    companion object {
        private const val keyHashLength = 32
    }

    private val argon2 = Argon2KeyDerivation()

    override val version: AlgorithmVersion
        get() = AlgorithmVersion.V2

    override suspend fun encrypt(password: String, text: String): String? = runCatching {
        val salt = argon2.generateSalt()
        val keyBytes = argon2.hash(password, salt, keyHashLength)
        val key: SecretKey = SecretKeySpec(keyBytes, 0, keyBytes.size, KeyProperties.KEY_ALGORITHM_AES)

        val cipher = getCipher().apply {
            init(Cipher.ENCRYPT_MODE, key)
        }
        val gcmParams = cipher.parameters.getParameterSpec(GCMParameterSpec::class.java)
        val encryptedText = cipher.doFinal(text.compressGzip())
        join(
            ivBase64 = gcmParams.iv.encodeBase64(),
            tLen = gcmParams.tLen,
            encryptedTextBase64 = encryptedText.encodeBase64(),
            saltBase64 = salt.encodeBase64(),
        )
    }.onFailure {
        Napier.e(tag = "CryptographyV2", message = "encrypt($password, $text): ${it.message}", throwable = it)
    }.getOrNull()

    override suspend fun decrypt(password: String, encryptedText: String): String? = runCatching {
        val encryptedTextComponents = split(encryptedText)

        val keyBytes = argon2.hash(password, encryptedTextComponents.saltBase64.decodeBase64(), keyHashLength)
        val key: SecretKey = SecretKeySpec(keyBytes, 0, keyBytes.size, KeyProperties.KEY_ALGORITHM_AES)

        val gcmParams = GCMParameterSpec(encryptedTextComponents.tLen, encryptedTextComponents.ivBase64.decodeBase64())
        val cipher = getCipher().apply {
            init(Cipher.DECRYPT_MODE, key, gcmParams)
        }
        val encryptedTextBytes = encryptedTextComponents.encryptedTextBase64.decodeBase64()
        cipher.doFinal(encryptedTextBytes).decompressGzip()
    }.onFailure {
        Napier.e(tag = "CryptographyV2", message = "encrypt($password, $encryptedText): ${it.message}", throwable = it)
    }.getOrNull()

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_GCM + "/" +
                    KeyProperties.ENCRYPTION_PADDING_NONE
        )
    }

    private fun join(saltBase64: String, ivBase64: String, tLen: Int, encryptedTextBase64: String): String {
        return "$saltBase64.$ivBase64.$tLen.$encryptedTextBase64"
    }

    private fun split(input: String): EncryptedTextComponents {
        val components = input.split(".")
        if (components.size != 4) throw IllegalStateException("Invalid encrypted text input: $input")
        return EncryptedTextComponents(
            saltBase64 = components[0],
            ivBase64 = components[1],
            tLen = components[2].toInt(),
            encryptedTextBase64 = components[3],
        )
    }

}

private data class EncryptedTextComponents(
    val saltBase64: String,
    val ivBase64: String,
    val tLen: Int,
    val encryptedTextBase64: String,
)