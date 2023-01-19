package io.github.nfdz.cryptool.ui.main

import androidx.compose.material3.SnackbarHostState
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.viewModel.EmptyEncryptionViewModel
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionState
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.platform.EmptyApplicationManager
import io.github.nfdz.cryptool.ui.platform.EmptyClipboardAndroid
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class MainScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val selectedEncryptionIds = setOf("1")
    private val encryptions = listOf(
        Encryption(
            "1",
            "Joe",
            "abc",
            AlgorithmVersion.V1,
            MessageSource.MANUAL,
            true,
            12,
            "abc",
            987688696768
        ),
        Encryption(
            "2",
            "Mark",
            "123",
            AlgorithmVersion.V2,
            MessageSource.MANUAL,
            false,
            0,
            "444",
            2345
        ),
    )

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                MainScreenContent(
                    viewModel = EmptyEncryptionViewModel,
                    applicationManager = EmptyApplicationManager,
                    clipboard = EmptyClipboardAndroid,
                    router = EmptyRouter,
                    snackbar = SnackbarHostState(),
                    state = EncryptionState(
                        encryptions = encryptions,
                        selectedEncryptionIds = setOf(),
                        initialized = true,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                MainScreenContent(
                    viewModel = EmptyEncryptionViewModel,
                    applicationManager = EmptyApplicationManager,
                    clipboard = EmptyClipboardAndroid,
                    router = EmptyRouter,
                    snackbar = SnackbarHostState(),
                    state = EncryptionState(
                        encryptions = encryptions,
                        selectedEncryptionIds = setOf(),
                        initialized = true,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun lightSelected() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                MainScreenContent(
                    viewModel = EmptyEncryptionViewModel,
                    applicationManager = EmptyApplicationManager,
                    clipboard = EmptyClipboardAndroid,
                    router = EmptyRouter,
                    snackbar = SnackbarHostState(),
                    state = EncryptionState(
                        encryptions = encryptions,
                        selectedEncryptionIds = selectedEncryptionIds,
                        initialized = true,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun darkSelected() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                MainScreenContent(
                    viewModel = EmptyEncryptionViewModel,
                    applicationManager = EmptyApplicationManager,
                    clipboard = EmptyClipboardAndroid,
                    router = EmptyRouter,
                    snackbar = SnackbarHostState(),
                    state = EncryptionState(
                        encryptions = encryptions,
                        selectedEncryptionIds = selectedEncryptionIds,
                        initialized = true,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun lightEmpty() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                MainScreenContent(
                    viewModel = EmptyEncryptionViewModel,
                    applicationManager = EmptyApplicationManager,
                    clipboard = EmptyClipboardAndroid,
                    router = EmptyRouter,
                    snackbar = SnackbarHostState(),
                    state = EncryptionState(
                        encryptions = listOf(),
                        selectedEncryptionIds = setOf(),
                        initialized = true,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun darkEmpty() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                MainScreenContent(
                    viewModel = EmptyEncryptionViewModel,
                    applicationManager = EmptyApplicationManager,
                    clipboard = EmptyClipboardAndroid,
                    router = EmptyRouter,
                    snackbar = SnackbarHostState(),
                    state = EncryptionState(
                        encryptions = listOf(),
                        selectedEncryptionIds = setOf(),
                        initialized = true,
                    ),
                )
            }
        }.assertSame()
    }

}