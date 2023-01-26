package io.github.nfdz.cryptool.service.tool

import android.content.Context
import androidx.navigation.NavController
import io.github.nfdz.cryptool.AppActivity
import io.github.nfdz.cryptool.service.ball.OverlayBallService
import io.github.nfdz.cryptool.ui.RouterBase


class RouterOverlay(
    navController: NavController,
    private val context: Context,
    private val minimizeOverlay: () -> Unit,
    private val closeOverlay: () -> Unit,
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
        closeOverlay()
        AppActivity.startNew(context)
    }

    override fun navigateToUrl(url: String) {
        assert(false)
    }

}