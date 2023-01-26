package io.github.nfdz.cryptool.ui.extension

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import io.github.nfdz.cryptool.shared.gatekeeper.entity.TutorialInformation
import io.github.nfdz.cryptool.ui.R

fun Context.getTutorialInformation(): TutorialInformation {
    return TutorialInformation(
        title = getString(R.string.welcome_tutorial_title),
        messages = listOf(
            getString(R.string.welcome_tutorial_message_1),
            getString(R.string.welcome_tutorial_message_2),
            getString(R.string.welcome_tutorial_message_3),
            getString(R.string.welcome_tutorial_message_4),
            getString(R.string.welcome_tutorial_message_5),
        ),
    )
}

fun Context.navigateToAppSystemSettings() {
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
}