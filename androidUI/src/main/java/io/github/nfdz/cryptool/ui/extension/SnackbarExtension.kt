package io.github.nfdz.cryptool.ui.extension

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun SnackbarHostState.showSnackbarAsync(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
    MainScope().launch {
        this@showSnackbarAsync.showSnackbar(message = message, duration = duration)
    }
}

