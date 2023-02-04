package io.github.nfdz.cryptool.shared.platform.time

import androidx.annotation.VisibleForTesting

actual object Clock {

    @VisibleForTesting
    var nowInMillisForTesting: Long? = null

    actual fun nowInSeconds(): Long = (nowInMillis() / 1000.0).toLong()
    actual fun nowInMillis(): Long = nowInMillisForTesting ?: System.currentTimeMillis()
}