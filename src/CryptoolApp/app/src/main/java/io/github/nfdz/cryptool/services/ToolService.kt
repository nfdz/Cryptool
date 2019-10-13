package io.github.nfdz.cryptool.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.BroadcastHelper
import io.github.nfdz.cryptool.common.utils.fadeIn
import io.github.nfdz.cryptool.common.utils.fadeOut
import io.github.nfdz.cryptool.views.cipher.CipherContract
import io.github.nfdz.cryptool.views.cipher.CipherViewImpl

class ToolService : Service() {

    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, ToolService::class.java))
        }
    }

    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private val layoutParams: WindowManager.LayoutParams by lazy { buildLayoutParams() }
    private val toolView: View by lazy {
        LayoutInflater.from(this).inflate(R.layout.floating_tool, null)
    }
    private val cipherView: CipherContract.View by lazy { CipherViewImpl(toolView, this) }
    private val bcReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            closeTool()
        }
    }


    override fun onCreate() {
        super.onCreate()
        windowManager.addView(toolView, layoutParams)
        val tvLogo: View = toolView.findViewById<View>(R.id.ft_tv_logo)
        val btnBall: View = toolView.findViewById<View>(R.id.ft_btn_ball)
        val btnClose: View = toolView.findViewById<View>(R.id.ft_btn_close)
        tvLogo.setOnClickListener { closeTool(launchApp = true) }
        btnBall.setOnClickListener { closeTool(launchBall = true) }
        btnClose.setOnClickListener { closeTool() }
        cipherView.onViewCreated()
        registerReceiver(bcReceiver, IntentFilter(BroadcastHelper.CLOSE_FLOATING_WINDOWS_ACTION))
        toolView.fadeIn()
    }


    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildLayoutParams(): WindowManager.LayoutParams {
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
        }
        params.gravity = Gravity.CENTER;
        return params
    }

    override fun onDestroy() {
        unregisterReceiver(bcReceiver)
        cipherView.onDestroyView()
        windowManager.removeView(toolView)
        super.onDestroy()
    }

    private fun closeTool(launchBall: Boolean = false, launchApp: Boolean = false) {
        toolView.fadeOut {
            if (launchBall) {
                BallService.start(this)
            }
            stopSelf()
        }
    }


}