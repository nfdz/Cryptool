package io.github.nfdz.cryptool.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import io.github.nfdz.cryptool.R


object ClipboardHelper {

    /**
     * Try to copy given text. If it is not possible, it will show a toast message.
     */
    fun copyText(
        context: Context,
        text: String
    ) {
        if (text.isNotEmpty()) {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(context.getString(R.string.cb_label), text)
            clipboard.setPrimaryClip(clip)
            context.toast(R.string.cb_copy_success)
        } else {
            context.toast(R.string.cb_copy_empty)
        }
    }

    /**
     * Try to paste text from clipboard in given destination.
     * If it is not possible, it will show a toast message.
     */
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

    fun clearClipboard(context: Context) {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboard == null || !clipboard.hasPrimaryClip()) {
            context.toast(R.string.cb_clear_empty)
        } else {
            clearClipboardQuietly(context.getString(R.string.cb_label), clipboard)
            context.toast(R.string.cb_clear_success)
        }
    }

    fun clearClipboardQuietly(label: String, clipboard: ClipboardManager?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            clipboard?.clearPrimaryClip()
        } else {
            val clip =
                ClipData.newPlainText(label, "\uD83D\uDC40")
            clipboard?.setPrimaryClip(clip)
        }
    }
}