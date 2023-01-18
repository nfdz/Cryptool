package io.github.nfdz.cryptool.shared.cryptography

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.platform.cryptography.CryptographyV2
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CryptographyV2Test {
    companion object {
        private const val password = "abc"
        private const val data =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
        private const val encryptedData =
            "z3C_OUR_lYdYpkbMZ_pawta0ehM.aOpLlw1CMyyh_aj7.128.V_V108PJzPv5hd4WZLQ7JBHOxJWyYPKQQFjELaXzBWCCChNUHOroeMzvgx9QZK_o1TR0PqYCqFpKFnMJR9sAeXaF_hB-Ik3vBn3fFUXFZyDLjx5hy9_dvgon6hnlUj4ic4b7VTbUEg068XJ2senGFsTm6RhJbCi-0YtV3Gcdwrbo1XdC5XMTDLp0BD08RO0IpciWUsMvr8ILODeCzmI_iVq3Xvk7ioTGJBJMr9jbphyHvAlt_L00ug"
        private const val encryptedEmpty = "eKh4VuQuzDBALymsmn1SKQ==\n"
    }

    @Test
    fun testVersion() = runTest {
        val instance = CryptographyV2()
        assertEquals(AlgorithmVersion.V2, instance.version)
    }

    @Test
    fun testEncryptAndDecrypt() = runTest {
        val instance = CryptographyV2()

        val encryptedData = instance.encrypt(password, data)
        val result = instance.decrypt(password, encryptedData!!)
        assertEquals(data, result)
    }

    @Test
    fun testEncryptWithNoPassword() = runTest {
        val instance = CryptographyV2()

        val encryptedData = instance.encrypt("", data)
        val result = instance.decrypt("", encryptedData!!)
        assertEquals(data, result)
    }

    @Test
    fun testEncryptAndDecryptWithNoData() = runTest {
        val instance = CryptographyV2()

        val encryptedData = instance.encrypt(password, "")
        val result = instance.decrypt(password, encryptedData!!)
        assertEquals("", result)
    }

    @Test
    fun testDecrypt() = runTest {
        val instance = CryptographyV2()

        val result = instance.decrypt(password, encryptedData)
        assertEquals(data, result)
    }

    @Test
    fun testDecryptWithNoPassword() = runTest {
        val instance = CryptographyV2()

        val result = instance.decrypt("", encryptedData)
        assertEquals(null, result)
    }

    @Test
    fun testDecryptWithInvalidData() = runTest {
        val instance = CryptographyV2()

        val result = instance.decrypt(password, "invalid-data")
        assertEquals(null, result)
    }
}