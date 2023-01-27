package io.github.nfdz.cryptool.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import io.github.nfdz.cryptool.extension.fadeIn

abstract class OverlayViewServiceBase : Service() {

    companion object {
        fun closeAll(context: Context) {
            context.sendBroadcast(Intent(closeAction))
        }

        private const val closeAction = "io.github.nfdz.cryptool.CLOSE_FLOATING_WINDOWS"
    }

    protected val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    protected val layoutParams: WindowManager.LayoutParams by lazy { buildLayoutParams() }
    private val bcReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            closeOverlay()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        registerReceiver(bcReceiver, IntentFilter(closeAction))
        windowManager.addView(view, layoutParams)
        view.fadeIn()
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
        windowManager.removeView(view)
        unregisterReceiver(bcReceiver)
        super.onDestroy()
    }

    abstract val view: View
    abstract fun closeOverlay()
    open fun optionalSetupLayoutParams(params: WindowManager.LayoutParams) {}
    open val overlayFullScreen: Boolean
        get() = false
    abstract val windowLayoutFlags: Int

}