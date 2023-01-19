package io.github.nfdz.cryptool.ui.migration

import androidx.compose.material3.SnackbarHostState
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationInformation
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.EmptyGatekeeperViewModel
import io.github.nfdz.cryptool.ui.DarkColorScheme
import io.github.nfdz.cryptool.ui.LightColorScheme
import io.github.nfdz.cryptool.ui.platform.EmptyLegacyPinCodeManager
import io.github.nfdz.cryptool.ui.test.TestEntry
import org.junit.Rule
import org.junit.Test

class LegacyMigrationScreenScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun light() {
        rule.setCompose {
            TestEntry(colorScheme = LightColorScheme) {
                LegacyMigrationScreenContent(
                    snackbar = SnackbarHostState(),
                    viewModel = EmptyGatekeeperViewModel,
                    legacyPinCodeManager = EmptyLegacyPinCodeManager,
                    activity = null,
                    legacyMigrationInfo = LegacyMigrationInformation(true),
                )
            }
        }.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun dark() {
        rule.setCompose {
            TestEntry(colorScheme = DarkColorScheme) {
                LegacyMigrationScreenContent(
                    snackbar = SnackbarHostState(),
                    viewModel = EmptyGatekeeperViewModel,
                    legacyPinCodeManager = EmptyLegacyPinCodeManager,
                    activity = null,
                    legacyMigrationInfo = LegacyMigrationInformation(true),
                )
            }
        }.assertSame()
    }

}