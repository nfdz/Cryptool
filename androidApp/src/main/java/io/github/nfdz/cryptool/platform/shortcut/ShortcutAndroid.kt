package io.github.nfdz.cryptool.platform.shortcut

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.R

object ShortcutAndroid {

    private const val id = "cryptool_open_overlay_ball"
    private const val openAction = "io.github.nfdz.cryptool.OPEN_FLOATING_BALL"

    fun create(context: Context) = runCatching {
        val shortcut = ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(context.getString(R.string.shortcut_title))
            .setLongLabel(context.getString(R.string.shortcut_description))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_open_overlay))
            .setIntent(Intent(openAction))
            .build()
        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }.onFailure {
        Napier.e(tag = "Shortcut", message = "Create error: ${it.message}", throwable = it)
    }

    fun delete(context: Context) = runCatching {
        ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(id))
    }.onFailure {
        Napier.e(tag = "Shortcut", message = "Delete error: ${it.message}", throwable = it)
    }

    fun shouldOpen(intent: Intent?): Boolean {
        return intent?.action == openAction
    }

}