package io.github.nfdz.cryptool.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.extension.fadeIn
import io.github.nfdz.cryptool.extension.hasOverlayPermission

abstract class OverlayViewServiceBase : Service() {

    protected val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    protected val layoutParams: WindowManager.LayoutParams by lazy { buildLayoutParams() }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (!hasOverlayPermission()) {
            showErrorToast(getString(R.string.service_permission_error))
            stopSelf()
            return
        }
        runCatching {
            windowManager.addView(view, layoutParams)
            view.fadeIn()
        }.onFailure {
            showErrorToast(getString(R.string.service_unexpected_error))
            stopSelf()
        }
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun buildLayoutParams(): WindowManager.LayoutParams {
        val sizeConfig = if (overlayFullScreen) WindowManager.LayoutParams.MATCH_PARENT
        else WindowManager.LayoutParams.WRAP_CONTENT
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                sizeConfig,
                sizeConfig,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                windowLayoutFlags,
                PixelFormat.TRANSLUCENT,
            )
        } else {
            WindowManager.LayoutParams(
                sizeConfig,
                sizeConfig,
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
                windowLayoutFlags,
                PixelFormat.TRANSLUCENT,
            )
        }
        optionalSetupLayoutParams(params)
        return params
    }

    override fun onDestroy() {
        runCatching { windowManager.removeView(view) }.onFailure {
            Napier.e(tag = "OverlayViewServiceBase", message = "onDestroy error", throwable = it)
        }
        super.onDestroy()
    }

    abstract val view: View
    open fun optionalSetupLayoutParams(params: WindowManager.LayoutParams) {}
    open val overlayFullScreen: Boolean
        get() = false
    abstract val windowLayoutFlags: Int

}