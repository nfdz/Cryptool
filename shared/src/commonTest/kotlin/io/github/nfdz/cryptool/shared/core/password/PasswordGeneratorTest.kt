package io.github.nfdz.cryptool.shared.core.password

import io.github.nfdz.cryptool.shared.test.runCoroutineTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordGeneratorTest {

    @Test
    fun testGenerate() = runCoroutineTest {
        val result = PasswordGenerator.generate()
        assertEquals(20, result.length)
    }

}