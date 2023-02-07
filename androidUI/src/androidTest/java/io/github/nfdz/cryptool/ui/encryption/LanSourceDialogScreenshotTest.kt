package io.github.nfdz.cryptool.ui.encryption

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class LanSourceDialogScreenshotTest {

    private val networkAddresses = listOf("1.2.3.4")
    private val serverPort = 123
    private val serverSlot = 3

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun lightManual() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                LanSourceDialogContent(networkAddresses, serverPort, serverSlot) {}
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun darkManual() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                LanSourceDialogContent(networkAddresses, serverPort, serverSlot) {}
            }
        }.assertSame()
    }
}