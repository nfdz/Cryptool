package io.github.nfdz.cryptool.ui

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationInformation
import io.github.nfdz.cryptool.shared.gatekeeper.entity.WelcomeInformation
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperState
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.ui.gatekeeper.GatekeeperScreen
import io.github.nfdz.cryptool.ui.migration.LegacyMigrationScreen
import io.github.nfdz.cryptool.ui.welcome.WelcomeScreen

@Composable
fun AppEntryPoint(
    activity: FragmentActivity,
    router: Router,
    navController: NavHostController,
    gatekeeperViewModel: GatekeeperViewModel,
) {
    AppTheme {
        val state = gatekeeperViewModel.observeState().collectAsState().value
        Crossfade(targetState = state.toScreen()) {
            when (it) {
                is AppEntryScreen.LegacyMigration -> {
                    LegacyMigrationScreen(
                        viewModel = gatekeeperViewModel,
                        activity = activity,
                        legacyMigrationInfo = it.data,
                    )
                }
                AppEntryScreen.Main -> {
                    NavHost(
                        navController = navController, startDestination = router.startDestination
                    ) {
                        router.applyConfiguration(this)
                    }
                }
                is AppEntryScreen.Welcome -> {
                    WelcomeScreen(
                        viewModel = gatekeeperViewModel,
                        information = it.data,
                    )
                }
                AppEntryScreen.Gatekeeper -> GatekeeperScreen(
                    activity,
                    gatekeeperViewModel,
                    hasCode = state.hasCode,
                    canUseBiometricAccess = state.canUseBiometricAccess,
                    loadingAccess = state.loadingAccess,
                    supportAdvancedFeatures = router.supportAdvancedFeatures(),
                )
            }
        }
    }
}

private sealed class AppEntryScreen {
    object Main : AppEntryScreen()
    object Gatekeeper : AppEntryScreen()
    data class LegacyMigration(val data: LegacyMigrationInformation) : AppEntryScreen()
    data class Welcome(val data: WelcomeInformation) : AppEntryScreen()
}

private fun GatekeeperState.toScreen(): AppEntryScreen {
    return when {
        isOpen -> AppEntryScreen.Main
        canMigrateFromLegacy != null -> AppEntryScreen.LegacyMigration(canMigrateFromLegacy!!)
        welcome != null -> AppEntryScreen.Welcome(welcome!!)
        else -> AppEntryScreen.Gatekeeper
    }
}