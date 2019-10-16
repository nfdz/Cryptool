package io.github.nfdz.cryptool.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.*
import io.github.nfdz.cryptool.screens.main.MainActivity
import io.github.nfdz.cryptool.views.ToolViewBase
import io.github.nfdz.cryptool.views.cipher.CipherViewImpl
import io.github.nfdz.cryptool.views.hash.HashViewImpl
import io.github.nfdz.cryptool.views.keys.KeysViewImpl
import timber.log.Timber


class ToolService : Service() {

    companion object {
        fun start(context: Context, action: String?) {
            context.startService(Intent(context, ToolService::class.java).setAction(action))
        }
    }

    private var action: String? = null
    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private val layoutParams: WindowManager.LayoutParams by lazy { buildLayoutParams() }
    private val toolView: View by lazy {
        LayoutInflater.from(this).inflate(R.layout.floating_tool, null)
    }
    private var tool: ToolViewBase? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        action = intent?.action
        windowManager.addView(toolView, layoutParams)
        val container: ViewGroup = toolView.findViewById(R.id.ft_container)
        when (action) {
            OPEN_KEYS_BALL_ACTION -> {
                val keysView = LayoutInflater.from(this).inflate(R.layout.keys_tool, null)
                keysView.setBackgroundResource(R.drawable.shape_tool_body_round)
                container.addView(keysView)
                tool = KeysViewImpl(keysView, this)
            }
            OPEN_HASH_BALL_ACTION -> {
                val hashView = LayoutInflater.from(this).inflate(R.layout.hash_tool, null)
                hashView.setBackgroundResource(R.drawable.shape_tool_body_round)
                container.addView(hashView)
                tool = HashViewImpl(hashView, this)
            }
            else -> {
                val cipherView = LayoutInflater.from(this).inflate(R.layout.cipher_tool, null)
                cipherView.setBackgroundResource(R.drawable.shape_tool_body_round)
                container.addView(cipherView)
                tool = CipherViewImpl(cipherView, this)
            }
        }
        val tvLogo: View = toolView.findViewById<View>(R.id.ft_tv_logo)
        val btnBall: View = toolView.findViewById<View>(R.id.ft_btn_ball)
        val btnClose: View = toolView.findViewById<View>(R.id.ft_btn_close)
        tvLogo.setOnClickListener { closeTool(launchApp = true) }
        btnBall.setOnClickListener { closeTool(launchBall = true) }
        btnClose.setOnClickListener { closeTool() }
        tool?.onViewCreated()
        toolView.fadeIn()
        return super.onStartCommand(intent, flags, startId)
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
        params.gravity = Gravity.CENTER
        return params
    }

    override fun onDestroy() {
        tool?.onDestroyView()
        try {
            windowManager.removeView(toolView)
        } catch (e: Exception) {
            Timber.e(e)
        }
        super.onDestroy()
    }

    private fun closeTool(launchBall: Boolean = false, launchApp: Boolean = false) {
        toolView.fadeOut {
            stopSelf()
            if (launchBall) {
                BallService.start(this, action)
            } else if (launchApp) {
                MainActivity.startNewActivity(this)
            } else {
                stopApp()
            }
        }
    }

}