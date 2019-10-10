package io.github.nfdz.cryptool.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import io.github.nfdz.cryptool.R


object ClipboardHelper {

    fun copyText(
        context: Context,
        clipLabel: String,
        text: String
    ) {
        if (text.isNotEmpty()) {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(clipLabel, text)
            clipboard.primaryClip = clip
            context.toast(R.string.cb_copy_success)
        } else {
            context.toast(R.string.cb_copy_empty)
        }
    }

    fun pasteText(context: Context, destination: (String) -> (Unit)) {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboard == null || !clipboard.hasPrimaryClip()) {
            context.toast(R.string.cb_paste_empty)
        } else {
            val item = clipboard.primaryClip?.getItemAt(0)
            val pasteData = item?.text
            if (pasteData?.isNotEmpty() == true) {
                destination(pasteData.toString())
            } else {
                context.toast(R.string.cb_paste_empty)
            }
        }
    }
}