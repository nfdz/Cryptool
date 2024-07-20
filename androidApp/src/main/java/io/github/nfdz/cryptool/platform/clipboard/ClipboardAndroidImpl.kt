package io.github.nfdz.cryptool.platform.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.showSnackbarAsync
import io.github.nfdz.cryptool.ui.platform.ClipboardAndroid
import java.util.concurrent.atomic.AtomicBoolean

object ClipboardAndroidImpl : ClipboardAndroid {

    private val hasAppData = AtomicBoolean(false)

    private const val label = "Cryptool"

    private fun getClipboardManager(context: Context): ClipboardManager? =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

    override fun set(context: Context, snackbar: SnackbarHostState, text: String) {
        hasAppData.set(true)

        val clipboard = getClipboardManager(context)
        if (clipboard != null && text.isNotEmpty()) {
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
            snackbar.showSnackbarAsync(
                context.getString(R.string.cb_copy_success_snackbar),
                duration = SnackbarDuration.Short
            )
        } else {
            snackbar.showSnackbarAsync(
                context.getString(R.string.cb_copy_empty_snackbar),
                duration = SnackbarDuration.Short
            )
        }
    }

    override fun clear(context: Context, snackbar: SnackbarHostState) {
        hasAppData.set(false)

        val clipboard = getClipboardManager(context)

        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                clipboard!!.clearPrimaryClip()
            } else {
                val clip = ClipData.newPlainText("-", "-")
                clipboard!!.setPrimaryClip(clip)
            }
        }

        snackbar.showSnackbarAsync(
            context.getString(R.string.cb_clear_success_snackbar),
            duration = SnackbarDuration.Short
        )
    }

    override fun hasAppData(): Boolean = hasAppData.get()

//    fun hasAppData(context: Context): Boolean {
//        val clipboard = getClipboardManager(context)
//
//        if (clipboard != null && clipboard.hasPrimaryClip()) {
//            return clipboard.primaryClip?.description?.label == label
//        }
//        return false
//    }

//    suspend fun get(context: Context, snackbar: SnackbarHostState): String? {
//        val clipboard = getClipboardManager(context)
//        if (clipboard == null || !clipboard.hasPrimaryClip()) {
//            snackbar.showSnackbarAsync(context.getString(R.string.), duration = SnackbarDuration.Short)
//            return null
//        }
//
//        val item = clipboard.primaryClip?.getItemAt(0)
//        val itemText = item?.text
//        return if (itemText.isNullOrBlank()) {
//            snackbar.showSnackbarAsync(context.getString(R.string.), duration = SnackbarDuration.Short)
//            null
//        } else {
//            itemText.toString()
//        }
//    }
}