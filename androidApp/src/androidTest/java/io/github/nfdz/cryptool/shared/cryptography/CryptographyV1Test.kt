package io.github.nfdz.cryptool.shared.cryptography

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.platform.cryptography.CryptographyV1
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CryptographyV1Test {

    companion object {
        private const val password = "abc"
        private const val data =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
        private const val encryptedData =
            "VwRC8fCHgewu/IO6X+QXM6C45w39okDhybUa355BNAej2+xM2ehaCOt+s2xBScMZLlbyzk/SWDTx\nbgWU/U4A5INrN+/1CJOZfu1b8+rbrUlxBg7m/l1Qjd9HvMoyHWl6/DnTHH/fdbck/IUBA4SGqK2X\nmtbLr5vFoCt1h0cBa1A2QmMCWM4l1bVMjct9qD+94hR/oAPR/HSnh5bqE6LaXPWUB3Yapbn6N7YH\n6sLYLwU3+8heJVYMcQbiQx3ojGcYcM/LxDpHwygH6slWGZmpIQkEqZuD0iIcpc32aCUarwIikzlB\n70bguSLzVTGoyD0t\n"
        private const val encryptedEmpty = "eKh4VuQuzDBALymsmn1SKQ==\n"
    }

    @Test
    fun testVersion() = runTest {
        val instance = CryptographyV1()
        assertEquals(AlgorithmVersion.V1, instance.version)
    }

    @Test
    fun testEncrypt() = runTest {
        val instance = CryptographyV1()

        val result = instance.encrypt(password, data)
        assertEquals(encryptedData, result)
    }

    @Test
    fun testEncryptNoPassword() = runTest {
        val instance = CryptographyV1()

        val result = instance.encrypt("", data)
        assertEquals(null, result)
    }

    @Test
    fun testEncryptNoData() = runTest {
        val instance = CryptographyV1()

        val result = instance.encrypt(password, "")
        assertEquals(encryptedEmpty, result)
    }

    @Test
    fun testDecrypt() = runTest {
        val instance = CryptographyV1()

        val result = instance.decrypt(password, encryptedData)
        assertEquals(data, result)
    }

    @Test
    fun testDecryptWithNoPassword() = runTest {
        val instance = CryptographyV1()

        val result = instance.decrypt("", encryptedData)
        assertEquals(null, result)
    }

    @Test
    fun testDecryptWithNoData() = runTest {
        val instance = CryptographyV1()

        val result = instance.decrypt(password, "")
        assertEquals("", result)
    }

    @Test
    fun testDecryptWithInvalidData() = runTest {
        val instance = CryptographyV1()

        val result = instance.decrypt(password, "invalid-data")
        assertEquals(null, result)
    }
}