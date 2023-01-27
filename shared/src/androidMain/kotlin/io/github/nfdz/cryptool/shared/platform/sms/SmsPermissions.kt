package io.github.nfdz.cryptool.shared.platform.sms

import android.Manifest

val smsPermissions = listOf(
    Manifest.permission.SEND_SMS,
    Manifest.permission.RECEIVE_SMS,
    Manifest.permission.READ_SMS,
)