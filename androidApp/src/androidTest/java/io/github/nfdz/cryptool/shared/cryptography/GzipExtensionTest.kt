package io.github.nfdz.cryptool.shared.cryptography

import io.github.nfdz.cryptool.shared.platform.cryptography.compressGzip
import io.github.nfdz.cryptool.shared.platform.cryptography.decompressGzip
import io.github.nfdz.cryptool.test.decodeHex
import io.github.nfdz.cryptool.test.encodeHex
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.io.EOFException

class GzipExtensionTest {

    companion object {
        private const val empty = ""
        private const val emptyCompressedHex = "1f8b080000000000000003000000000000000000"

        private const val data =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
        private const val dataCompressedHex =
            "1f8b0800000000000000258f518e03310843afe203543d497ff700344195a510660259edf197e9fc81b09fcdcb971a78c436741fbe104c88693ed07c86b6d4dc0bd279301ae7073a58c7d05e0628779877a4da5166cec6cebe67622786bc0b0fcd1bad30f94c810c9e5b9ef849e8a4151bc66bf8ad55ec817333303d72ed0efdd3d59892f4893d8658f39b7c8918bc92be481e25864a15b7eae4f7031595cf7fc9f2ee0de7000000"
    }

    @Test
    fun testCompressEmpty() {
        val result = empty.compressGzip().encodeHex()
        assertEquals(emptyCompressedHex, result)
    }

    @Test
    fun testCompress() {
        val result = data.compressGzip().encodeHex()
        assertEquals(dataCompressedHex, result)
    }

    @Test
    fun testDecompressEmpty() {
        val result = emptyCompressedHex.decodeHex().decompressGzip()
        assertEquals(empty, result)
    }

    @Test
    fun testDecompress() {
        val result = dataCompressedHex.decodeHex().decompressGzip()
        assertEquals(data, result)
    }

    @Test(expected = EOFException::class)
    fun testDecompressError() {
        "12".decodeHex().decompressGzip()
    }
}