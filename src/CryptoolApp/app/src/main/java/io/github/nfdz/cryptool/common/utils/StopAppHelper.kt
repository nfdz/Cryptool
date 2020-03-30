package io.github.nfdz.cryptool.common.utils

import android.content.ClipboardManager
import android.os.Handler
import android.os.Looper
import android.os.Process

const val STOP_APP_AUTO_MILLIS = 15000L

fun stopApp(label: String, clipboard: ClipboardManager?) {
    ClipboardHelper.clearClipboardQuietly(label, clipboard)
    Process.killProcess(Process.myPid())
}

private class StopAppTask(val label: String, val clipboard: ClipboardManager?) : Runnable {
    var cancelled: Boolean = false

    override fun run() {
        if (!cancelled) stopApp(label, clipboard)
    }

    fun cancel() {
        cancelled = true
    }
}

private var autoStopApp = true
private var scheduledTask: StopAppTask? = null

fun scheduleStopApp(label: String, clipboard: ClipboardManager?) {
    scheduledTask?.cancel()
    scheduledTask = null
    if (autoStopApp) {
        scheduledTask = StopAppTask(label, clipboard)
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