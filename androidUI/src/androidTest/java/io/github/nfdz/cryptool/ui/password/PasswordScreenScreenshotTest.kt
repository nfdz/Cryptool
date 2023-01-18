package io.github.nfdz.cryptool.ui.password

import androidx.compose.material3.SnackbarHostState
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.viewModel.EmptyPasswordViewModel
import io.github.nfdz.cryptool.shared.password.viewModel.PasswordState
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.platform.EmptyApplicationManager
import io.github.nfdz.cryptool.ui.platform.EmptyClipboardAndroid
import org.junit.Rule
import org.junit.Test

class PasswordScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val selectedTags = setOf("Test-1", "Test-2")
    private val passwords = listOf(
        Password(
            "1",
            "Foo",
            "123",
            setOf("Test-1", "Test-2")
        ),
        Password(
            "2",
            "Joe",
            "abc",
            setOf("Test-1")
        ),
        Password(
            "3",
            "Mark",
            "zxcv",
            setOf("Test-2")
        ),
        Password(
            "4",
            "Lorem",
            "ipsum",
            setOf("Test-3")
        ),
    )
    private val tags = passwords.map { it.tags }.flatten().toSet().toList()

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                PasswordScreenContent(
                    snackbar = SnackbarHostState(),
                    clipboard = EmptyClipboardAndroid,
                    applicationManager = EmptyApplicationManager,
                    viewModel = EmptyPasswordViewModel,
                    router = EmptyRouter,
                    state = PasswordState(
                        initialized = true,
                        passwords = passwords,
                        selectedTags = setOf(),
                        tags = tags,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            AppTheme(colorScheme = DarkColorScheme) {
                PasswordScreenContent(
                    snackbar = SnackbarHostState(),
                    clipboard = EmptyClipboardAndroid,
                    applicationManager = EmptyApplicationManager,
                    viewModel = EmptyPasswordViewModel,
                    router = EmptyRouter,
                    state = PasswordState(
                        initialized = true,
                        passwords = passwords,
                        selectedTags = setOf(),
                        tags = tags,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun lightSelected() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                PasswordScreenContent(
                    snackbar = SnackbarHostState(),
                    clipboard = EmptyClipboardAndroid,
                    applicationManager = EmptyApplicationManager,
                    viewModel = EmptyPasswordViewModel,
                    router = EmptyRouter,
                    state = PasswordState(
                        initialized = true,
                        passwords = passwords,
                        selectedTags = selectedTags,
                        tags = tags,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun darkSelected() {
        rule.setCompose {
            AppTheme(colorScheme = DarkColorScheme) {
                PasswordScreenContent(
                    snackbar = SnackbarHostState(),
                    clipboard = EmptyClipboardAndroid,
                    applicationManager = EmptyApplicationManager,
                    viewModel = EmptyPasswordViewModel,
                    router = EmptyRouter,
                    state = PasswordState(
                        initialized = true,
                        passwords = passwords,
                        selectedTags = selectedTags,
                        tags = tags,
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun lightEmpty() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                PasswordScreenContent(
                    snackbar = SnackbarHostState(),
                    clipboard = EmptyClipboardAndroid,
                    applicationManager = EmptyApplicationManager,
                    viewModel = EmptyPasswordViewModel,
                    router = EmptyRouter,
                    state = PasswordState(
                        initialized = true,
                        passwords = emptyList(),
                        selectedTags = setOf(),
                        tags = emptyList(),
                    ),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun darkEmpty() {
        rule.setCompose {
            AppTheme(colorScheme = DarkColorScheme) {
                PasswordScreenContent(
                    snackbar = SnackbarHostState(),
                    clipboard = EmptyClipboardAndroid,
                    applicationManager = EmptyApplicationManager,
                    viewModel = EmptyPasswordViewModel,
                    router = EmptyRouter,
                    state = PasswordState(
                        initialized = true,
                        passwords = emptyList(),
                        selectedTags = setOf(),
                        tags = emptyList(),
                    ),
                )
            }
        }.assertSame()
    }
}