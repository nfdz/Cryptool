package io.github.nfdz.cryptool.ui.extension

import android.content.Context
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.shared.gatekeeper.entity.TutorialInformation

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
