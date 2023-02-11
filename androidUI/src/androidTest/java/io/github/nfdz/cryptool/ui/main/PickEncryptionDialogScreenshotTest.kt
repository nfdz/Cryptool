package io.github.nfdz.cryptool.ui.main

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class PickEncryptionDialogScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val incomingData =
        "-99Vkg91QTKxpapuXQjqwJ5Mw.fDcHFT5r2nF42S0nq28PQQ.128.rbbgZVnc9f5hE7KHaL_t_pMgmulmWLYl9HrCqb8IhdnasIUmA"
    private val encryptions = listOf(
        Encryption("", "Conversation 1", "", AlgorithmVersion.V2, null, false, 0, "", 0L),
        Encryption("", "Conversation 2", "", AlgorithmVersion.V2, null, false, 0, "", 0L),
        Encryption("", "Conversation 3", "", AlgorithmVersion.V2, null, false, 0, "", 0L),
    )

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                PickEncryptionDialogContent(incomingData, encryptions) {}
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                PickEncryptionDialogContent(incomingData, encryptions) {}
            }
        }.assertSame()
    }
}