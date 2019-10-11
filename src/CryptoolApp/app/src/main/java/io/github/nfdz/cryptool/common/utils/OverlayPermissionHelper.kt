package io.github.nfdz.cryptool.common.utils


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

class OverlayPermissionHelper(private val activity: Activity, private val callback: Callback?) {

    interface Callback {
        fun onPermissionGranted()
        fun onPermissionDenied()
    }

    companion object {
        private val CODE_PERMISSION_REQUEST = 2839
    }

    fun request() {
        if (!hasPermission()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)
            )
            activity.startActivityForResult(intent, CODE_PERMISSION_REQUEST)
        } else {
            callback?.onPermissionGranted()
        }
    }

    fun onActivityResult(requestCode: Int) {
        if (requestCode == CODE_PERMISSION_REQUEST) {
            if (hasPermission()) {
                callback?.onPermissionGranted()
            } else {
                callback?.onPermissionDenied()
            }
        }
    }

    fun navigateToSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)
            )
        } else {
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.packageName)
            )
        }
        activity.startActivity(intent)
    }

    fun hasPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(activity)
    }

}
