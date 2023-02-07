package io.github.nfdz.cryptool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import io.github.nfdz.cryptool.extension.hasOverlayPermission
import io.github.nfdz.cryptool.platform.broadcast.MessageEventBroadcast
import io.github.nfdz.cryptool.platform.permission.OverlayPermissionImpl
import io.github.nfdz.cryptool.platform.shortcut.ShortcutAndroid
import io.github.nfdz.cryptool.service.ball.OverlayBallService
import io.github.nfdz.cryptool.service.tool.OverlayToolService
import io.github.nfdz.cryptool.shared.core.constant.AppUrl
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperAction
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.shared.platform.version.VersionProvider
import io.github.nfdz.cryptool.ui.AppEntryPoint
import io.github.nfdz.cryptool.ui.extension.openUrl
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

        fun close(context: Context) {
            context.sendBroadcast(Intent(closeAction).setPackage(context.packageName))
        }

        private const val closeAction = "io.github.nfdz.cryptool.CLOSE_ACTIVITY"
    }

    private val versionProvider: VersionProvider by inject()
    private val overlayPermission = OverlayPermissionImpl(this)
    private val gatekeeperViewModel: GatekeeperViewModel by inject()
    private val msgEventReceiver = MessageEventBroadcast.createReceiver(get())
    private val closeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context?.packageName != intent?.`package`) return
            finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(closeReceiver, IntentFilter(closeAction))
        MessageEventBroadcast.registerReceiver(this, msgEventReceiver)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        if (!launchShortcut(intent)) {
            closeOverlay()
            updateShortcut()
            askThis()
            setContent {
                val navController = rememberNavController()
                val router = RouterApp(navController, this, overlayPermission)
                AppEntryPoint(this, router, navController, gatekeeperViewModel)
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(closeReceiver)
        MessageEventBroadcast.unregisterReceiver(this, msgEventReceiver)
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        launchShortcut(intent)
    }

    private fun launchShortcut(intent: Intent?): Boolean {
        if (packageName != intent?.`package`) return false
        return if (ShortcutAndroid.shouldOpen(intent) && hasOverlayPermission()) {
            OverlayBallService.start(this)
            true
        } else {
            false
        }
    }

    private fun updateShortcut() {
        if (hasOverlayPermission()) {
            ShortcutAndroid.create(this)
        } else {
            ShortcutAndroid.delete(this)
        }
    }

    private fun closeOverlay() {
        OverlayBallService.close(this)
        OverlayToolService.close(this)
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

    private fun askThis() = launch {
        val newVersionToNotify = versionProvider.getRemoteNewVersion() ?: return@launch
        AlertDialog.Builder(this@AppActivity)
            .setTitle(getString(R.string.app_name) + " " + newVersionToNotify)
            .setMessage(R.string.main_notify_new_github_version)
            .setPositiveButton(R.string.main_notify_new_github_version_download) { dialog, _ ->
                versionProvider.setNotifiedRemoteVersion(newVersionToNotify)
                openUrl(AppUrl.downloadGithubLatestVersion)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                versionProvider.setNotifiedRemoteVersion(newVersionToNotify)
                dialog.dismiss()
            }
            .show()
    }
}