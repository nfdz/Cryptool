package io.github.nfdz.cryptool.common.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class CryptographyHelper {
    companion object {
        private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val CIPHER = "AES"
        private const val AES_KEY_LENGTH_BITS = 128
        private const val BASE64_FLAGS = Base64.NO_WRAP

    }

    fun encrypt(plaintext: String, passphrase: String): String {
        val aesCipherForEncryption = Cipher.getInstance(CIPHER_TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKeyFromPassphrase(passphrase))
        }
        val byteCipherText = aesCipherForEncryption.doFinal(plaintext.toByteArray())
        return String(Base64.encode(byteCipherText, BASE64_FLAGS))
    }

    fun decrypt(ciphertext: String, passphrase: String): String {
        val aesCipherForDecryption = Cipher.getInstance(CIPHER_TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKeyFromPassphrase(passphrase))
        }
        val byteCipherText = Base64.decode(ciphertext.toByteArray(), BASE64_FLAGS)
        val bytePlainText = aesCipherForDecryption.doFinal(byteCipherText)
        return String(bytePlainText)
    }

    private fun getKeyFromPassphrase(passphrase: String): SecretKeySpec {
        val confidentialityKey = passphrase.toByteArray()
        return SecretKeySpec(confidentialityKey, 0, confidentialityKey.size, CIPHER)
    }
}