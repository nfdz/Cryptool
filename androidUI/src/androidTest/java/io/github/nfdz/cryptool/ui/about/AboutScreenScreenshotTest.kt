package io.github.nfdz.cryptool.ui.about

import androidx.compose.material3.SnackbarHostState
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.platform.version.EmptyVersionProvider
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class AboutScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                AboutScreenContent(
                    snackbar = SnackbarHostState(),
                    router = EmptyRouter,
                    versionName = "3.0.0-rc",
                    versionProvider = EmptyVersionProvider,
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                AboutScreenContent(
                    snackbar = SnackbarHostState(),
                    router = EmptyRouter,
                    versionName = "3.0.0-rc",
                    versionProvider = EmptyVersionProvider,
                )
            }
        }.assertSame()
    }

}