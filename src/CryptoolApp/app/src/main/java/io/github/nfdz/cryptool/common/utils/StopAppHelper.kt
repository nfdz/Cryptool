package io.github.nfdz.cryptool.common.utils

import android.os.Handler
import android.os.Looper
import android.os.Process

const val STOP_APP_AUTO_MILLIS = 20000L

fun stopApp() {
    Process.killProcess(Process.myPid())
}

private class StopAppTask : Runnable {
    var cancelled: Boolean = false

    override fun run() {
        if (!cancelled) stopApp()
    }

    fun cancel() {
        cancelled = true
    }
}

private var autoStopApp = true
private var scheduledTask: StopAppTask? = null

fun scheduleStopApp() {
    scheduledTask?.cancel()
    scheduledTask = null
    if (autoStopApp) {
        scheduledTask = StopAppTask()
        Handler(Looper.getMainLooper()).postDelayed(scheduledTask, STOP_APP_AUTO_MILLIS)
    }
}

fun unscheduleStopApp() {
    scheduledTask?.let {
        it.cancel()
        Handler(Looper.getMainLooper()).removeCallbacks(it)
    }
    scheduledTask = null
}

fun enableAutoStopApp() {
    autoStopApp = true
}

fun disableAutoStopApp() {
    autoStopApp = false
}