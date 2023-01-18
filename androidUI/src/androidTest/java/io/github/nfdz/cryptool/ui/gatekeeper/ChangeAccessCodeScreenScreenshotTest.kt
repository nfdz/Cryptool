package io.github.nfdz.cryptool.ui.gatekeeper

import androidx.compose.material3.SnackbarHostState
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.EmptyGatekeeperViewModel
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperState
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.LightColorScheme
import org.junit.Rule
import org.junit.Test

class ChangeAccessCodeScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            AppTheme(colorScheme = LightColorScheme) {
                ChangeAccessCodeScreenContent(
                    snackbar = SnackbarHostState(),
                    viewModel = EmptyGatekeeperViewModel,
                    router = EmptyRouter,
                    state = GatekeeperState(
                        isOpen = true,
                        hasCode = true,
                        welcome = null,
                        canUseBiometricAccess = true,
                        canMigrateFromLegacy = null,
                        loadingAccess = false,
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
                ChangeAccessCodeScreenContent(
                    snackbar = SnackbarHostState(),
                    viewModel = EmptyGatekeeperViewModel,
                    router = EmptyRouter,
                    state = GatekeeperState(
                        isOpen = true,
                        hasCode = true,
                        welcome = null,
                        canUseBiometricAccess = true,
                        canMigrateFromLegacy = null,
                        loadingAccess = false,
                    )
                )
            }
        }.assertSame()
    }

}