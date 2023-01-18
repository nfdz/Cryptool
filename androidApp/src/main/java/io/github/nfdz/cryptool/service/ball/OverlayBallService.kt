package io.github.nfdz.cryptool.service.ball

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.*
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.extension.fadeOut
import io.github.nfdz.cryptool.extension.themed
import io.github.nfdz.cryptool.platform.lifecycle.ApplicationManagerImpl
import io.github.nfdz.cryptool.service.OverlayViewServiceBase
import io.github.nfdz.cryptool.service.tool.OverlayToolService
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperAction
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

@SuppressLint("InflateParams")
class OverlayBallService : OverlayViewServiceBase() {

    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, OverlayBallService::class.java))
        }

        private const val lastPositionXKey = "ball_last_position_x"
        private const val lastPositionYKey = "ball_last_position_y"
    }

    private val gatekeeperViewModel: GatekeeperViewModel by inject()
    private val storage: KeyValueStorage by inject()

    override val view: View by lazy {
        LayoutInflater.from(this.themed()).inflate(R.layout.overlay_ball, null)
    }

    override val windowLayoutFlags: Int
        get() = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

    private var lastAction: Int = 0
    private var initialPosition: BallPosition = BallPosition(0, 0)
    private var initialTouchPosition: TouchPosition = TouchPosition(0f, 0f)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun optionalSetupLayoutParams(params: WindowManager.LayoutParams) {
        super.optionalSetupLayoutParams(params)
        params.x = storage.getInt(lastPositionXKey, 0)
        params.y = storage.getInt(lastPositionYKey, 0)
    }

    override fun onCreate() {
        super.onCreate()
        setupView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() {
        view.findViewById<View>(R.id.ball_icon).setOnTouchListener { _, event -> handleTouchEvent(event) }
        view.findViewById<View>(R.id.ball_close).setOnClickListener { closeBall(stopApp = true) }
    }

    private fun handleTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialPosition = BallPosition(layoutParams)
                initialTouchPosition = TouchPosition(event)
                lastAction = event.action
                true
            }
            MotionEvent.ACTION_UP -> {
                if (lastAction == MotionEvent.ACTION_DOWN || initialTouchPosition == TouchPosition(event)) {
                    closeBall(openOverlayTool = true)
                }
                lastAction = event.action
                true
            }
            MotionEvent.ACTION_MOVE -> {
                layoutParams.x = initialPosition.x + (event.rawX - initialTouchPosition.x).roundToInt()
                layoutParams.y = initialPosition.y + (event.rawY - initialTouchPosition.y).roundToInt()
                windowManager.updateViewLayout(view, layoutParams)
                lastAction = event.action
                true
            }
            else -> false
        }
    }

    override fun closeOverlay() = closeBall()

    private fun closeBall(openOverlayTool: Boolean = false, stopApp: Boolean = false) {
        gatekeeperViewModel.dispatch(GatekeeperAction.PushAccessValidity)
        saveLastPosition()
        view.fadeOut {
            stopSelf()
            when {
                openOverlayTool -> OverlayToolService.start(this)
                stopApp -> ApplicationManagerImpl.stopApp()
            }
        }
    }

    private fun saveLastPosition() {
        storage.putInt(lastPositionXKey, layoutParams.x)
        storage.putInt(lastPositionYKey, layoutParams.y)
    }

}

private data class BallPosition(val x: Int, val y: Int) {
    constructor(params: WindowManager.LayoutParams) : this(params.x, params.y)
}

private data class TouchPosition(val x: Float, val y: Float) {
    constructor(event: MotionEvent) : this(event.rawX, event.rawY)
}