package io.github.nfdz.cryptool.ui.about

import androidx.compose.material3.SnackbarHostState
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.LightColorScheme
import org.junit.Rule
import org.junit.Test

class LibrariesScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val data = listOf(
        LibraryJson(
            name = "Library 1",
            description = "This is library 1",
            version = "1.2.3",
            developers = listOf("Android"),
            url = "http://android.com",
            licenses = listOf(LicenseJson("Apache", "url")),
        ),
        LibraryJson(
            name = "Library 2",
            description = null,
            version = "1.2.3",
            developers = listOf("Android", "Google"),
            url = null,
            licenses = listOf(LicenseJson("Apache", "url"), LicenseJson("MIT", "url")),
        ),
    )

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                LibrariesScreenContent(
                    snackbar = SnackbarHostState(),
                    router = EmptyRouter,
                    state = data,
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            AppTheme(colorScheme = DarkColorScheme) {
                LibrariesScreenContent(
                    snackbar = SnackbarHostState(),
                    router = EmptyRouter,
                    state = data,
                )
            }
        }.assertSame()
    }

}