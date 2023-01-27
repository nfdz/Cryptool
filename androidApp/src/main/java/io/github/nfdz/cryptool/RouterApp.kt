package io.github.nfdz.cryptool

import androidx.navigation.NavHostController
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.extension.openUrl
import io.github.nfdz.cryptool.platform.permission.OverlayPermission
import io.github.nfdz.cryptool.service.ball.OverlayBallService
import io.github.nfdz.cryptool.ui.RouterBase

class RouterApp(
    navController: NavHostController,
    private val activity: AppActivity,
    private val overlayPermission: OverlayPermission,
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
