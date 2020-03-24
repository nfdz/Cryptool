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

/**
 * This service has the responsability of showing a tool view on the screen over any app.
 * This will block and prevent that user click outside the tool.
 * The tool is selected according with given intent action.
 */
class ToolService : Service() {

    companion object {
        fun start(context: Context, action: String?) {
            context.startService(Intent(context, ToolService::class.java).setAction(action))
        }
    }

    private var started: Boolean = false
    private var action: String? = null
    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private val layoutParams: WindowManager.LayoutParams by lazy { buildLayoutParams() }
    private val toolView: View by lazy {
        LayoutInflater.from(this).inflate(R.layout.floating_tool, null)
    }
    private var tool: ToolViewBase? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!started) {
            started = true
            action = intent?.action
            windowManager.addView(toolView, layoutParams)
            val container: ViewGroup = toolView.findViewById(R.id.ft_container)
            tool = createTool(container)
            val tvLogo: View = toolView.findViewById(R.id.ft_tv_logo)
            val btnBall: View = toolView.findViewById(R.id.ft_btn_ball)
            val btnClose: View = toolView.findViewById(R.id.ft_btn_close)
            tvLogo.setOnClickListener { closeTool(launchApp = true) }
            btnBall.setOnClickListener { closeTool(launchBall = true) }
            btnClose.setOnClickListener { closeTool() }
            tool?.onViewCreated()
            toolView.fadeIn()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createTool(container: ViewGroup) = when (action) {
        OPEN_KEYS_BALL_ACTION -> {
            val keysView = LayoutInflater.from(this).inflate(R.layout.keys_tool, null)
            keysView.setBackgroundResource(getBackgroundRes())
            container.addView(keysView)
            KeysViewImpl(keysView, this)
        }
        OPEN_HASH_BALL_ACTION -> {
            val hashView = LayoutInflater.from(this).inflate(R.layout.hash_tool, null)
            hashView.setBackgroundResource(getBackgroundRes())
            container.addView(hashView)
            HashViewImpl(hashView, this)
        }
        else -> {
            val cipherView = LayoutInflater.from(this).inflate(R.layout.cipher_tool, null)
            cipherView.setBackgroundResource(getBackgroundRes())
            container.addView(cipherView)
            CipherViewImpl(cipherView, this)
        }
    }

    private fun getBackgroundRes() =
        if (isNightUiMode() == true) {
            R.drawable.shape_tool_body_round_dark
        } else {
            R.drawable.shape_tool_body_round_light
        }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildLayoutParams(): WindowManager.LayoutParams {
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_SECURE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_SECURE,
                PixelFormat.TRANSLUCENT
            )
        }
        params.gravity = Gravity.CENTER
        return params
    }

    override fun onDestroy() {
        tool?.onDestroyView()
        tool = null
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
            when {
                launchBall -> BallService.start(this, action)
                launchApp -> MainActivity.startNewActivity(this)
                else -> stopApp(getString(R.string.cb_label), getClipboard())
            }
        }
    }

}