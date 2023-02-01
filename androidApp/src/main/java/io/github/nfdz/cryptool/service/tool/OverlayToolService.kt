package io.github.nfdz.cryptool.service.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.nfdz.cryptool.extension.fadeIn
import io.github.nfdz.cryptool.extension.fadeOut
import io.github.nfdz.cryptool.extension.noRippleClickable
import io.github.nfdz.cryptool.platform.broadcast.MessageEventBroadcast
import io.github.nfdz.cryptool.service.OverlayComposeViewServiceBase
import io.github.nfdz.cryptool.service.ball.OverlayBallService
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperAction
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.Router
import io.github.nfdz.cryptool.ui.gatekeeper.GatekeeperScreen
import io.github.nfdz.cryptool.ui.supportAdvancedFeatures
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class OverlayToolService : OverlayComposeViewServiceBase() {
    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, OverlayToolService::class.java))
        }

        fun close(context: Context) {
            context.sendBroadcast(Intent(closeAction).setPackage(context.packageName))
        }

        private const val closeAction = "io.github.nfdz.cryptool.CLOSE_OVERLAY_TOOL"
    }

    private val gatekeeperViewModel: GatekeeperViewModel by inject()
    private val msgEventReceiver = MessageEventBroadcast.createReceiver(get())
    private val closeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context?.packageName != intent?.`package`) return
            closeOverlay()
        }
    }

    override val overlayFullScreen: Boolean
        get() = true
    override val windowLayoutFlags: Int
        get() = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_SECURE

    override fun onCreate() {
        super.onCreate()
        registerReceiver(closeReceiver, IntentFilter(closeAction))
        MessageEventBroadcast.registerReceiver(this, msgEventReceiver)
        OverlayBallService.close(this)
    }

    override fun onDestroy() {
        unregisterReceiver(closeReceiver)
        MessageEventBroadcast.unregisterReceiver(this, msgEventReceiver)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (startId > 1) {
            restoreOverlay()
            OverlayBallService.close(this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @Composable
    override fun OverlayContent() {
        val navController = rememberNavController()
        val router = RouterOverlay(
            navController = navController,
            context = this,
            minimizeOverlay = ::minimizeOverlay,
        )
        val darkShadow = Color(0xFF191818)
        val largeRadialGradient = object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val biggerDimension = maxOf(size.height, size.width)
                return RadialGradientShader(
                    colors = listOf(darkShadow, Color.Transparent),
                    center = size.center,
                    radius = biggerDimension / 2f,
                    colorStops = listOf(0f, 0.95f)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(largeRadialGradient)
                    .noRippleClickable {
                        router.navigateToOverlayBall()
                    }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = 300.dp, height = 400.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AppTheme {
                    ToolEntryPoint(router, navController)
                }
            }
        }
    }

    @Composable
    private fun ToolEntryPoint(router: Router, navController: NavHostController) {
        val state = gatekeeperViewModel.observeState().collectAsState().value
        Crossfade(targetState = state) {
            when {
                it.isOpen -> {
                    NavHost(
                        navController = navController,
                        startDestination = router.startDestination,
                    ) {
                        router.applyConfiguration(this)
                    }
                }
                else -> GatekeeperScreen(
                    null,
                    gatekeeperViewModel,
                    hasCode = state.hasCode,
                    canUseBiometricAccess = state.canUseBiometricAccess,
                    loadingAccess = state.loadingAccess,
                    supportAdvancedFeatures = router.supportAdvancedFeatures(),
                )
            }
        }
    }

    private fun minimizeOverlay() {
        view.fadeOut {
            view.visibility = View.GONE
        }
    }

    private fun restoreOverlay() {
        view.visibility = View.VISIBLE
        view.fadeIn()
    }

    private fun closeOverlay() {
        gatekeeperViewModel.dispatch(GatekeeperAction.PushAccessValidity)
        view.fadeOut {
            stopSelf()
        }
    }
}