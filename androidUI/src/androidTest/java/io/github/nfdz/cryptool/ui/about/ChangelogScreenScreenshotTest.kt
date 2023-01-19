package io.github.nfdz.cryptool.ui.about

import androidx.compose.material3.SnackbarHostState
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.platform.version.VersionInformation
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class ChangelogScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val data = listOf(
        VersionInformation("2.0.0", "Lorem ipsum c"),
        VersionInformation("1.2.0", "Lorem ipsum b"),
        VersionInformation("1.0.0", "Lorem ipsum a"),
    )

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                ChangelogScreenContent(snackbar = SnackbarHostState(), router = EmptyRouter, data)
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                ChangelogScreenContent(snackbar = SnackbarHostState(), router = EmptyRouter, data)
            }
        }.assertSame()
    }

}