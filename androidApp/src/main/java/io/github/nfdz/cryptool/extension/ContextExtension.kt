package io.github.nfdz.cryptool.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import androidx.appcompat.view.ContextThemeWrapper
import io.github.nfdz.cryptool.R

fun Context.themed(): Context = ContextThemeWrapper(this, R.style.Theme_Cryptool)
fun Context.openUrl(url: String): Result<Unit> = runCatching {
    startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).putExtra(Browser.EXTRA_APPLICATION_ID, packageName)
    )
}
