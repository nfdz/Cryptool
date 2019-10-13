package io.github.nfdz.cryptool.common.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class CryptographyHelper {
    companion object {
        private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val CIPHER = "AES"
        private const val AES_KEY_LENGTH_BITS = 256
        private const val DUMMY_SALT_BASE = "ljk4356jh57we74365rg"
        private const val DUMMY_IV_BASE = "2323362g4b5kh2345fas"
        private const val DUMMY_ITERATION_COUNT = 73
        private const val KEY_GEN_ALGORITHM = "PBKDF2WithHmacSHA1"

        private const val BASE64_FLAGS = Base64.NO_WRAP

    }

    fun encrypt(plaintext: String, passphrase: String): String {
        val aesCipherForEncryption = Cipher.getInstance(CIPHER_TRANSFORMATION)
        aesCipherForEncryption.init(
            Cipher.ENCRYPT_MODE,
            getKeyFromPassphrase(passphrase),
            getDummyIv(aesCipherForEncryption)
        )
        val byteCipherText = aesCipherForEncryption.doFinal(plaintext.toByteArray())
        return String(Base64.encode(byteCipherText, BASE64_FLAGS))
    }

    fun decrypt(ciphertext: String, passphrase: String): String {
        val aesCipherForDecryption = Cipher.getInstance(CIPHER_TRANSFORMATION)
        aesCipherForDecryption.init(
            Cipher.DECRYPT_MODE,
            getKeyFromPassphrase(passphrase),
            getDummyIv(aesCipherForDecryption)
        )
        val byteCipherText = Base64.decode(ciphertext.toByteArray(), BASE64_FLAGS)
        val bytePlainText = aesCipherForDecryption.doFinal(byteCipherText)
        return String(bytePlainText)
    }

    private fun getKeyFromPassphrase(passphrase: String): SecretKeySpec {
        val salt = ByteArray(16)
        val saltBase = DUMMY_SALT_BASE.toByteArray()
        for (i in salt.indices) {
            if (i < saltBase.size) {
                salt[i] = saltBase[i]
            }
        }
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, DUMMY_ITERATION_COUNT, AES_KEY_LENGTH_BITS)
        val f = SecretKeyFactory.getInstance(KEY_GEN_ALGORITHM)
        val key = f.generateSecret(spec).encoded
        return SecretKeySpec(key, CIPHER)
    }

    private fun getDummyIv(cipher: Cipher): IvParameterSpec {
        val iv = ByteArray(cipher.blockSize)
        val ivBase = DUMMY_IV_BASE.toByteArray()
        for (i in iv.indices) {
            if (i < ivBase.size) {
                iv[i] = ivBase[i]
            }
        }
        return IvParameterSpec(iv)
    }
}