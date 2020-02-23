package io.github.nfdz.cryptool.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.view.*
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.*
import kotlin.math.roundToInt

/**
 * This service has the responsability of showing an icon ball on the screen over any apps.
 * It will not block the user input, so the user could use another app meanwhile.
 * This ball can be moved, closed (stop service) and opened (stop this service and launch
 * tool service)
 */
class BallService : Service() {

    companion object {
        fun start(context: Context, action: String?) {
            context.startService(Intent(context, BallService::class.java).setAction(action))
        }

        fun stop(context: Context) {
            val intent = Intent()
            intent.action = CLOSE_FLOATING_WINDOWS_ACTION
            context.sendBroadcast(intent)
        }

        private const val CLOSE_FLOATING_WINDOWS_ACTION =
            "io.github.nfdz.cryptool.CLOSE_FLOATING_WINDOWS"
        private const val RIGHT_GRAVITY = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        private const val LEFT_GRAVITY = Gravity.CENTER_VERTICAL or Gravity.LEFT
        const val DEFAULT_GRAVITY = RIGHT_GRAVITY
    }

    private var action: String? = null
    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private val touchXThreshold: Float by lazy { getXThreshold() }
    private val layoutParams: WindowManager.LayoutParams by lazy { buildLayoutParams() }
    private val prefs: PreferencesHelper by lazy { PreferencesHelper(this) }
    private val ballView: View by lazy {
        LayoutInflater.from(this).inflate(R.layout.floating_ball, null)
    }
    private val bcReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            closeBall(avoidStopProcess = true)
        }
    }

    private var lastAction: Int = 0
    private var initialY: Int = 0
    private var initialTouchY: Float = 0f

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        disableAutoStopApp()
        unscheduleStopApp()
        windowManager.addView(ballView, layoutParams)
        val ballIcon: View = ballView.findViewById<View>(R.id.ball_iv_icon)
        val closeIcon: View = ballView.findViewById<View>(R.id.ball_iv_close)
        ballIcon.setOnTouchListener { _, event -> handleTouchEvent(event) }
        closeIcon.setOnClickListener { closeBall() }
        registerReceiver(bcReceiver, IntentFilter(CLOSE_FLOATING_WINDOWS_ACTION))
        ballView.fadeIn()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        action = intent?.action
        return super.onStartCommand(intent, flags, startId)
    }

    private fun getXThreshold(): Float {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        return width / 2f
    }

    private fun buildLayoutParams(): WindowManager.LayoutParams {
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }
        params.gravity = prefs.getLastBallGravity()
        params.y = prefs.getLastBallPosition()
        return params
    }

    override fun onDestroy() {
        unregisterReceiver(bcReceiver)
        windowManager.removeView(ballView)
        super.onDestroy()
    }

    private fun handleTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // initial position.
                initialY = layoutParams.y
                // touch location
                initialTouchY = event.rawY
                lastAction = event.action
                true
            }
            MotionEvent.ACTION_UP -> {
                if (lastAction == MotionEvent.ACTION_DOWN || initialTouchY == event.rawY) {
                    closeBall(launchFloatingTool = true)
                }
                lastAction = event.action
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.rawX >= touchXThreshold && layoutParams.gravity == LEFT_GRAVITY) {
                    layoutParams.gravity = RIGHT_GRAVITY
                } else if (event.rawX < touchXThreshold && layoutParams.gravity == RIGHT_GRAVITY) {
                    layoutParams.gravity = LEFT_GRAVITY
                }
                layoutParams.y = initialY + (event.rawY - initialTouchY).roundToInt()
                windowManager.updateViewLayout(ballView, layoutParams)
                lastAction = event.action
                true
            }
            else -> false
        }
    }

    private fun closeBall(launchFloatingTool: Boolean = false, avoidStopProcess: Boolean = false) {
        prefs.setLastBallPosition(layoutParams.y)
        prefs.setLastBallGravity(layoutParams.gravity)
        ballView.fadeOut {
            stopSelf()
            if (launchFloatingTool) {
                ToolService.start(this, action)
            } else if (!avoidStopProcess) {
                stopApp()
            }
        }
    }

}
