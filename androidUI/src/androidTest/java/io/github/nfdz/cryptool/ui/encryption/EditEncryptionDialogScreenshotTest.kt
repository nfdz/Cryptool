package io.github.nfdz.cryptool.ui.encryption

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.viewModel.EmptyEncryptionViewModel
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class EditEncryptionDialogScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val encryptionToEdit = Encryption(
        "",
        "Conversation A",
        "secret password",
        AlgorithmVersion.V2,
        null,
        false,
        0,
        "",
        0L,
    )

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                EditEncryptionContent(encryptionToEdit, EmptyEncryptionViewModel) {}
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                EditEncryptionContent(encryptionToEdit, EmptyEncryptionViewModel) {}
            }
        }.assertSame()
    }
}