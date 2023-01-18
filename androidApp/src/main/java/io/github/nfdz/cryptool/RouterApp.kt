package io.github.nfdz.cryptool

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavHostController
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.extension.openUrl
import io.github.nfdz.cryptool.platform.export.ExportManager
import io.github.nfdz.cryptool.platform.import.ImportManager
import io.github.nfdz.cryptool.platform.permission.OverlayPermission
import io.github.nfdz.cryptool.service.ball.OverlayBallService
import io.github.nfdz.cryptool.shared.core.export.ExportConfiguration
import io.github.nfdz.cryptool.shared.core.import.ImportConfiguration
import io.github.nfdz.cryptool.ui.RouterBase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RouterApp(
    navController: NavHostController,
    private val activity: AppActivity,
    private val overlayPermission: OverlayPermission,
    private val exportManager: ExportManager,
    private val importManager: ImportManager,
) : RouterBase(navController) {

    override val isOverlayMode: Boolean = false

    override suspend fun navigateToOverlayPermission(): Boolean {
        return overlayPermission.request()
    }

    override fun navigateToOverlayPermissionSettings() {
        overlayPermission.navigateToSettings()
    }

    override fun navigateToOverlayBall() {
        OverlayBallService.start(activity)
        activity.finishAffinity()
    }

    override fun navigateToExportData(snackbar: SnackbarHostState, code: String?, configuration: ExportConfiguration) {
        MainScope().launch {
            exportManager.exportData(snackbar, code, configuration)
        }
    }

    override fun navigateToImportData(snackbar: SnackbarHostState, code: String?, configuration: ImportConfiguration) {
        MainScope().launch {
            importManager.importData(snackbar, code, configuration)
        }
    }

    override fun exitOverlay() {
        assert(false)
    }

    override fun navigateToUrl(url: String) {
        assert(url.isNotBlank()) { "Empty URL" }
        activity.openUrl(url).onFailure {
            Napier.e(tag = "Router", message = "Open URL error: $url", throwable = it)
        }
    }

}
