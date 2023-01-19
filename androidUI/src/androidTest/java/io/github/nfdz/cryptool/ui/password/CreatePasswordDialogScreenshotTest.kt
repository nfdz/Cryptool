package io.github.nfdz.cryptool.ui.password

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.password.viewModel.EmptyPasswordViewModel
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class CreatePasswordDialogScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                CreatePasswordContent(viewModel = EmptyPasswordViewModel) {}
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                CreatePasswordContent(viewModel = EmptyPasswordViewModel) {}
            }
        }.assertSame()
    }

}