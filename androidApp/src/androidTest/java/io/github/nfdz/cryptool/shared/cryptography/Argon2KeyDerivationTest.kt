package io.github.nfdz.cryptool.shared.cryptography

import io.github.nfdz.cryptool.shared.platform.cryptography.Argon2KeyDerivation
import io.github.nfdz.cryptool.test.decodeHex
import io.github.nfdz.cryptool.test.encodeHex
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class Argon2KeyDerivationTest {

    companion object {
        private const val password = "abc"
        private const val saltHex = "707611e33acdbf01d0b793179291e15c4801d055"
        private const val hashLength = 20
        private const val keyHex = "91aee50f117f7b0883cbd280b8cc2fe33d0d408f"
        private const val keyEmptyPwHex = "3f552005a498af1bb93e7e1af4b3ba78034f0fab"
    }

    @Test
    fun testHash() = runTest {
        val instance = Argon2KeyDerivation()

        val result = instance.hash(password = password, salt = saltHex.decodeHex(), hashLength = hashLength)

        assertEquals(20, result.size)
        assertEquals(keyHex, result.encodeHex())
    }

    @Test
    fun testHashWithEmptyPassword() = runTest {
        val instance = Argon2KeyDerivation()

        val result = instance.hash(password = "", salt = saltHex.decodeHex(), hashLength = hashLength)

        assertEquals(20, result.size)
        assertEquals(keyEmptyPwHex, result.encodeHex())
    }

    @Test(expected = Exception::class)
    fun testHashWithEmptySalt() = runTest {
        val instance = Argon2KeyDerivation()

        instance.hash(password = password, salt = ByteArray(0), hashLength = hashLength)
    }

    @Test(expected = Exception::class)
    fun testHashWithNoLength() = runTest {
        val instance = Argon2KeyDerivation()

        instance.hash(password = "", salt = saltHex.decodeHex(), hashLength = 0)
    }

    @Test
    fun testGenerateSalt() = runTest {
        val instance = Argon2KeyDerivation()
        assertEquals(20, instance.generateSalt().size)
    }
}