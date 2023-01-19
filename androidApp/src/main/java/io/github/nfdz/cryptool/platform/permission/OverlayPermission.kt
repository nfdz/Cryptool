package io.github.nfdz.cryptool.platform.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import io.github.nfdz.cryptool.AppActivity
import io.github.nfdz.cryptool.platform.shortcut.ShortcutAndroid
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface OverlayPermission {
    fun hasPermission(): Boolean
    suspend fun request(): Boolean
    fun navigateToSettings()
}

class OverlayPermissionImpl(private val activity: AppActivity) : OverlayPermission {

    private val getResult = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onResult()
        onResult = {}
    }
    private var onResult: () -> Unit = {}

    override suspend fun request(): Boolean {
        if (!hasPermission()) {
            return suspendCancellableCoroutine { continuation ->
                onResult = {
                    val state = hasPermission()
                    if (state) {
                        ShortcutAndroid.create(activity)
                    }
                    continuation.resume(state)
                }
                getResult.launch(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.packageName)
                    )
                )
            }
        }
        return true
    }

    override fun navigateToSettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + activity.packageName)
        )
        activity.startActivity(intent)
    }

    override fun hasPermission(): Boolean {
        return Settings.canDrawOverlays(activity)
    }

}