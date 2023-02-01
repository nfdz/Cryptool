package io.github.nfdz.cryptool.extension

import android.content.Context
import androidx.appcompat.view.ContextThemeWrapper
import io.github.nfdz.cryptool.R

fun Context.themed(): Context = ContextThemeWrapper(this, R.style.Theme_Cryptool)