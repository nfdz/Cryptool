package io.github.nfdz.cryptool.ui.platform

import androidx.fragment.app.FragmentActivity

interface LegacyPinCodeManager {
    fun askCode(
        onSuccessListener: () -> (Unit),
        onDeleteListener: () -> (Unit),
        activity: FragmentActivity
    )
}

object EmptyLegacyPinCodeManager : LegacyPinCodeManager {
    override fun askCode(onSuccessListener: () -> Unit, onDeleteListener: () -> Unit, activity: FragmentActivity) {}
}