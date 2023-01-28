package io.github.nfdz.cryptool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import io.github.nfdz.cryptool.platform.broadcast.MessageEventBroadcast
import io.github.nfdz.cryptool.platform.permission.OverlayPermissionImpl
import io.github.nfdz.cryptool.platform.shortcut.ShortcutAndroid
import io.github.nfdz.cryptool.service.OverlayViewServiceBase
import io.github.nfdz.cryptool.service.ball.OverlayBallService
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperAction
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.ui.AppEntryPoint
import kotlinx.coroutines.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class AppActivity : FragmentActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    companion object {
        fun startNew(context: Context) {
            context.startActivity(Intent(context, AppActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    private val overlayPermission = OverlayPermissionImpl(this)
    private val gatekeeperViewModel: GatekeeperViewModel by inject()
    private val msgEventReceiver = MessageEventBroadcast.createReceiver(get())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageEventBroadcast.registerReceiver(this, msgEventReceiver)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        if (!launchShortcut(intent)) {
            OverlayViewServiceBase.closeAll(this)
            updateShortcut()
            setContent {
                val navController = rememberNavController()
                val router = RouterApp(navController, this, overlayPermission)
                AppEntryPoint(this, router, navController, gatekeeperViewModel)
            }
        }
    }

    override fun onDestroy() {
        MessageEventBroadcast.unregisterReceiver(this, msgEventReceiver)
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        launchShortcut(intent)
    }

    private fun launchShortcut(intent: Intent?): Boolean {
        if (packageName != intent?.`package`) return false
        return if (ShortcutAndroid.shouldOpen(intent)) {
            OverlayBallService.start(this)
            finishAffinity()
            true
        } else {
            false
        }
    }

    private fun updateShortcut() {
        if (overlayPermission.hasPermission()) {
            ShortcutAndroid.create(this)
        } else {
            ShortcutAndroid.delete(this)
        }
    }

    override fun onStart() {
        super.onStart()
        MainScope().launch {
            // Delay to avoid conflict closing the open services
            delay(1500)
            gatekeeperViewModel.dispatch(GatekeeperAction.CheckAccess)
        }
    }

    override fun onStop() {
        super.onStop()
        gatekeeperViewModel.dispatch(GatekeeperAction.PushAccessValidity)
    }
}