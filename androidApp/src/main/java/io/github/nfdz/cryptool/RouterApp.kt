package io.github.nfdz.cryptool

import android.content.Context
import androidx.navigation.NavHostController
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.platform.permission.OverlayPermission
import io.github.nfdz.cryptool.service.ball.OverlayBallService
import io.github.nfdz.cryptool.ui.RouterBase
import io.github.nfdz.cryptool.ui.extension.openUrl

class RouterApp(
    navController: NavHostController,
    private val context: Context,
    private val overlayPermission: OverlayPermission,
) : RouterBase(context, navController) {

    override val isOverlayMode: Boolean = false

    override suspend fun navigateToOverlayPermission(): Boolean {
        return overlayPermission.request()
    }

    override fun navigateToOverlayPermissionSettings() {
        overlayPermission.navigateToSettings()
    }

    override fun navigateToOverlayBall() {
        OverlayBallService.start(context)
    }

    override fun exitOverlay() {
        assert(false)
    }

    override fun navigateToUrl(url: String) {
        assert(url.isNotBlank()) { "Empty URL" }
        context.openUrl(url).onFailure {
            Napier.e(tag = "Router", message = "Open URL error: $url", throwable = it)
        }
    }

}
