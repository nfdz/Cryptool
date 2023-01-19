package io.github.nfdz.cryptool.shared.core.password

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordGeneratorTest {

    @Test
    fun testGenerate() = runTest {
        val result = PasswordGenerator.generate()
        assertEquals(20, result.length)
    }

}