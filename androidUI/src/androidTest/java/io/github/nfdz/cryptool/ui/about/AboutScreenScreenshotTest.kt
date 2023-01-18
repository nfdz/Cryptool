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

class AboutScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                AboutScreenContent(snackbar = SnackbarHostState(), router = EmptyRouter)
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            AppTheme(colorScheme = DarkColorScheme) {
                AboutScreenContent(snackbar = SnackbarHostState(), router = EmptyRouter)
            }
        }.assertSame()
    }

}