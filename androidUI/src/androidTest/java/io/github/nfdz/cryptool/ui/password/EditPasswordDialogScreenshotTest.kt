package io.github.nfdz.cryptool.ui.password

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.viewModel.EmptyPasswordViewModel
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class EditPasswordDialogScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val passwordToEdit = Password(
        "1",
        "Foo",
        "123",
        setOf("Test1", "Test2")
    )

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                EditPasswordContent(passwordToEdit, EmptyPasswordViewModel) {}
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                EditPasswordContent(passwordToEdit, EmptyPasswordViewModel) {}
            }
        }.assertSame()
    }

}