package io.github.nfdz.cryptool.common.utils

import android.content.Context
import android.content.Intent


object BroadcastHelper {

    val CLOSE_FLOATING_WINDOWS_ACTION = "io.github.nfdz.cryptool.CLOSE_FLOATING_WINDOWS"

    fun sendCloseFloatingWindowsBroadcast(context: Context) {
        val intent = Intent()
        intent.action = CLOSE_FLOATING_WINDOWS_ACTION
        context.sendBroadcast(intent)
    }

}
