package io.github.nfdz.cryptool.ui.encryption

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.encryption.viewModel.EmptyEncryptionViewModel
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class CreateEncryptionDialogScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                CreateEncryptionContent(viewModel = EmptyEncryptionViewModel) {}
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                CreateEncryptionContent(viewModel = EmptyEncryptionViewModel) {}
            }
        }.assertSame()
    }

}