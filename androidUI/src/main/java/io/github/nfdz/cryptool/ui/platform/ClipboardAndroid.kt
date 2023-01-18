package io.github.nfdz.cryptool.ui.platform

import android.content.Context
import androidx.compose.material3.SnackbarHostState

interface ClipboardAndroid {
    fun set(context: Context, snackbar: SnackbarHostState, text: String)
    fun clear(context: Context, snackbar: SnackbarHostState)
    fun hasAppData(): Boolean
}

object EmptyClipboardAndroid : ClipboardAndroid {
    override fun set(context: Context, snackbar: SnackbarHostState, text: String) {}
    override fun clear(context: Context, snackbar: SnackbarHostState) {}
    override fun hasAppData(): Boolean = false
}