package io.github.nfdz.cryptool.ui.welcome

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.WelcomeInformation
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.EmptyGatekeeperViewModel
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class WelcomeScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @Composable
    private fun getWelcomeInformation(): WelcomeInformation {
        return WelcomeInformation(
            title = stringResource(R.string.app_slogan),
            content = stringResource(R.string.welcome_main_description),
            welcomeTutorial = true,
        )
    }

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                WelcomeScreenContent(
                    snackbar = SnackbarHostState(),
                    viewModel = EmptyGatekeeperViewModel,
                    information = getWelcomeInformation(),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                WelcomeScreenContent(
                    snackbar = SnackbarHostState(),
                    viewModel = EmptyGatekeeperViewModel,
                    information = getWelcomeInformation(),
                )
            }
        }.assertSame()
    }
}