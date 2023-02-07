package io.github.nfdz.cryptool.extension

import android.content.Context
import android.provider.Settings
import androidx.appcompat.view.ContextThemeWrapper
import io.github.nfdz.cryptool.R

fun Context.themed(): Context = ContextThemeWrapper(this, R.style.Theme_Cryptool)

fun Context.hasOverlayPermission(): Boolean = Settings.canDrawOverlays(this)