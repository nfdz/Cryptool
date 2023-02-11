package io.github.nfdz.cryptool.service.tool

import android.content.Context
import android.content.Intent
import androidx.navigation.NavController
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.AppActivity
import io.github.nfdz.cryptool.service.ball.OverlayBallService
import io.github.nfdz.cryptool.ui.RouterBase
import io.github.nfdz.cryptool.ui.extension.openUrl

class RouterOverlay(
    navController: NavController,
    private val context: Context,
    private val minimizeOverlay: () -> Unit,
) : RouterBase(navController) {

    override val isOverlayMode: Boolean = true

    override suspend fun navigateToOverlayPermission(): Boolean {
        return true
    }

    override fun navigateToOverlayPermissionSettings() {
        assert(false)
    }

    override fun navigateToOverlayBall() {
        OverlayBallService.start(context)
        minimizeOverlay()
    }

    override fun exitOverlay() {
        AppActivity.startNew(context)
    }

    override fun navigateToUrl(url: String) {
        assert(url.isNotBlank()) { "Empty URL" }
        context.openUrl(url, extraFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK).onFailure {
            Napier.e(tag = "Router", message = "Open URL error: $url", throwable = it)
        }.onSuccess {
            navigateToOverlayBall()
        }
    }

}