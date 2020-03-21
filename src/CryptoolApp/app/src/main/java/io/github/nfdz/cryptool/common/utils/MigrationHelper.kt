package io.github.nfdz.cryptool.common.utils

import io.github.nfdz.cryptool.common.model.MigrationData


class MigrationHelper(
    private val prefs: PreferencesHelper,
    val data: MigrationData = MigrationData(
        theme = prefs.getThemeNightMode(),
        lastTab = prefs.getLastTab(),
        lastPassphrase = prefs.getLastPassphrase(),
        lastPassphraseLocked = prefs.wasLastPassphraseLocked(),
        lastOriginText = prefs.getLastOriginText(),
        lastBallPosition = prefs.getLastBallPosition(),
        lastBallGravity = prefs.getLastBallGravity(),
        lastHashOrigin = prefs.getLastHashOriginText(),
        keysLabel = prefs.getKeysLabel(),
        keysValue = prefs.getKeysValue()
    )
) {


    fun deployData() {
        prefs.setThemeNightMode(data.theme)
        prefs.setLastTab(data.lastTab)
        prefs.setLastPassphrase(data.lastPassphrase)
        prefs.setLastPassphraseLocked(data.lastPassphraseLocked)
        prefs.setLastOriginText(data.lastOriginText)
        prefs.setLastBallPosition(data.lastBallPosition)
        prefs.setLastBallGravity(data.lastBallGravity)
        prefs.setLastHashOriginText(data.lastHashOrigin)
        prefs.setKeysLabel(data.keysLabel)
        prefs.setKeysValue(data.keysValue)
    }
}