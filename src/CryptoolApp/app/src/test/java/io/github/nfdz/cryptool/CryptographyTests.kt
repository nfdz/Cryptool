package io.github.nfdz.cryptool

import io.github.nfdz.cryptool.common.utils.CryptographyHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * This junit class contains all tests about CryptographyHelper.
 */
class CryptographyTests {

    companion object {
        private const val DUMMY_TEXT =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"
        private const val DUMMY_PASS = "Lorem ipsum"
        private const val DUMMY_PROCESSED =
            "mcgVEtIPk+L5pJJ1G/TYl4+ZuzgqJ8BS2SdvqXz1FETBxotA0Fm3GhK4NpTjngIumU1Gk8AOQS8TuMOwZFnjk2dddRJG3yMgId7Cqv+TVInm17n0FGkGzcUHHngXkL+770Q5x7X4gqubfX0gjADwN/CE/ymxPusgLA1UdJF/4PA="
        private const val DUMMY_HASH =
            "29c03d361dde0dbff070f8efad6da05fcb72e0cc825c301ab8f0596224bc23c7"
    }

    /** Encrypt an already known processed text, output must be always the same */
    @Test
    fun encrypt_deterministic() {
        val crypto = CryptographyHelper()
        val origin = DUMMY_TEXT
        val expected = DUMMY_PROCESSED
        val passphrase = DUMMY_PASS
        val processed = crypto.encrypt(origin, passphrase)
        assertEquals(expected, processed)
    }

    /** Encrypt and decrypt text */
    @Test
    fun encrypt_decrypt() {
        val crypto = CryptographyHelper()
        val origin = DUMMY_TEXT
        val passphrase = DUMMY_PASS
        val processed = crypto.encrypt(origin, passphrase)
        assertEquals(origin, crypto.decrypt(processed, passphrase))
    }

    /** Decrypt an invalid encrypted text */
    @Test(expected = Exception::class)
    fun decrypt_error() {
        val crypto = CryptographyHelper()
        val processed = DUMMY_TEXT
        val passphrase = DUMMY_PASS
        crypto.decrypt(processed, passphrase)
    }

    /** Encrypt and decrypt a big bunch of random data */
    @Test
    fun encrypt_decrypt_random_long() {
        val crypto = CryptographyHelper()
        val size = 10000
        val origins = List(size) {
            UUID.randomUUID().toString()
        }
        val passwords = List(size) {
            UUID.randomUUID().toString()
        }
        val outputs = List(size) {
            crypto.encrypt(origins[it], passwords[it])
        }
        assertEquals(size, outputs.size)
        for (it in 0 until size) {
            assertEquals(origins[it], crypto.decrypt(outputs[it], passwords[it]))
        }
    }

    /** Hash an already known text, output must be always the same */
    @Test
    fun hash_deterministic() {
        val crypto = CryptographyHelper()
        val origin = DUMMY_TEXT
        val processed = crypto.hash(origin)
        val expected = DUMMY_HASH
        assertEquals(expected, processed)
    }

    /** Hash an already known text, output must be always the same */
    @Test
    fun hash_collision_random_long() {
        val crypto = CryptographyHelper()
        val size = 100000
        val outputs: Set<String> = List(size) {
            crypto.hash(it.toString())
        }.toSet()
        assertEquals(size, outputs.size)
    }

}
