package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.material3.SnackbarHostState
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.viewModel.EmptyMessageViewModel
import io.github.nfdz.cryptool.shared.message.viewModel.MessageState
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.platform.EmptyClipboardAndroid
import org.junit.Rule
import org.junit.Test

class EncryptionScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val encryption = Encryption(
        "11",
        "Joe",
        "Pw test",
        AlgorithmVersion.V2,
        MessageSource.MANUAL,
        false,
        0,
        "",
        0L,
    )

    private val selectedMessages = setOf("1", "5")
    private val messages = listOf(
        Message(
            id = "1",
            encryptionId = "11",
            message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            encryptedMessage = "ae42424339fn93555n55",
            timestampInMillis = 2,
            isFavorite = true,
            ownership = MessageOwnership.OTHER,
        ), Message(
            id = "3",
            encryptionId = "11",
            message = "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua",
            encryptedMessage = "42482408r2484f282438849f34f349f",
            timestampInMillis = 3,
            isFavorite = false,
            ownership = MessageOwnership.OWN,
        ), Message(
            id = "4",
            encryptionId = "11",
            message = "'Mark' ➡️ 'Joe'",
            encryptedMessage = "",
            timestampInMillis = 4,
            isFavorite = false,
            ownership = MessageOwnership.SYSTEM,
        ), Message(
            id = "5",
            encryptionId = "11",
            message = "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat",
            encryptedMessage = "96596789678967897689",
            timestampInMillis = 5,
            isFavorite = false,
            ownership = MessageOwnership.OWN,
        )
    )

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                EncryptionScreenContent(
                    snackbar = SnackbarHostState(),
                    encryptionId = "11",
                    initialEncryptionName = "Joe",
                    viewModel = EmptyMessageViewModel,
                    router = EmptyRouter,
                    clipboard = EmptyClipboardAndroid,
                    state = MessageState(
                        messages = messages,
                        selectedMessageIds = setOf(),
                        encryption = encryption,
                        visibility = true,
                    )
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            AppTheme(colorScheme = DarkColorScheme) {
                EncryptionScreenContent(
                    snackbar = SnackbarHostState(),
                    encryptionId = "11",
                    initialEncryptionName = "Joe",
                    viewModel = EmptyMessageViewModel,
                    router = EmptyRouter,
                    clipboard = EmptyClipboardAndroid,
                    state = MessageState(
                        messages = messages,
                        selectedMessageIds = setOf(),
                        encryption = encryption,
                        visibility = true,
                    )
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun lightSelected() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                EncryptionScreenContent(
                    snackbar = SnackbarHostState(),
                    encryptionId = "11",
                    initialEncryptionName = "Joe",
                    viewModel = EmptyMessageViewModel,
                    router = EmptyRouter,
                    clipboard = EmptyClipboardAndroid,
                    state = MessageState(
                        messages = messages,
                        selectedMessageIds = selectedMessages,
                        encryption = encryption,
                        visibility = true,
                    )
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun darkSelected() {
        rule.setCompose {
            AppTheme(colorScheme = DarkColorScheme) {
                EncryptionScreenContent(
                    snackbar = SnackbarHostState(),
                    encryptionId = "11",
                    initialEncryptionName = "Joe",
                    viewModel = EmptyMessageViewModel,
                    router = EmptyRouter,
                    clipboard = EmptyClipboardAndroid,
                    state = MessageState(
                        messages = messages,
                        selectedMessageIds = selectedMessages,
                        encryption = encryption,
                        visibility = true,
                    )
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun lightSourcePicker() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                EncryptionScreenContent(
                    snackbar = SnackbarHostState(),
                    encryptionId = "11",
                    initialEncryptionName = "Joe",
                    viewModel = EmptyMessageViewModel,
                    router = EmptyRouter,
                    clipboard = EmptyClipboardAndroid,
                    state = MessageState(
                        messages = messages,
                        selectedMessageIds = setOf(),
                        encryption = encryption.copy(source = null),
                        visibility = true,
                    )
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun darkSourcePicker() {
        rule.setCompose {
            AppTheme(colorScheme = DarkColorScheme) {
                EncryptionScreenContent(
                    snackbar = SnackbarHostState(),
                    encryptionId = "11",
                    initialEncryptionName = "Joe",
                    viewModel = EmptyMessageViewModel,
                    router = EmptyRouter,
                    clipboard = EmptyClipboardAndroid,
                    state = MessageState(
                        messages = messages,
                        selectedMessageIds = setOf(),
                        encryption = encryption.copy(source = null),
                        visibility = true,
                    )
                )
            }
        }.assertSame()
    }
}