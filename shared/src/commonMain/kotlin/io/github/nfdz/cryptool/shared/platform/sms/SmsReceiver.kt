package io.github.nfdz.cryptool.shared.platform.sms

import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import kotlinx.datetime.Clock

interface SmsReceiver {
    fun receivePendingMessage()
    fun afterReset()
}

object SmsReceiverPreferences {
    private const val lastReceivedBaselineMillisKey = "sms_last_received_baseline"
    private const val lastReceivedTimestampMillisKey = "sms_last_received_timestamp"

    fun getBaseline(storage: KeyValueStorage): Long {
        return storage.getLong(lastReceivedBaselineMillisKey, 0L)
    }

    fun setBaseline(storage: KeyValueStorage) {
        storage.putLong(lastReceivedBaselineMillisKey, Clock.System.now().toEpochMilliseconds())
    }

    fun getLastReceivedTimestamp(storage: KeyValueStorage): Long {
        return storage.getLong(lastReceivedTimestampMillisKey, getBaseline(storage))
    }

    fun setLastReceivedTimestamp(storage: KeyValueStorage, timestampInMillis: Long) {
        storage.putLong(lastReceivedTimestampMillisKey, timestampInMillis)
    }
}