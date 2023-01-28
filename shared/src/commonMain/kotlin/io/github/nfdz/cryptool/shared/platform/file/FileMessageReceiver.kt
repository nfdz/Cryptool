package io.github.nfdz.cryptool.shared.platform.file

import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import kotlinx.datetime.Clock

interface FileMessageReceiver {
    fun launchMessagesPolling(isOpen: () -> Boolean)
    fun afterReset()
}

object FileMessageReceiverPreferences {

    private const val lastReceivedBaselineMillisKey = "file_last_received_baseline"
    private const val lastReceivedTimestampMillisKey = "file_last_received_timestamp"

    fun setBaseline(storage: KeyValueStorage) {
        storage.putLong(lastReceivedBaselineMillisKey, Clock.System.now().toEpochMilliseconds())
    }

    fun getBaseline(storage: KeyValueStorage): Long = storage.getLong(lastReceivedBaselineMillisKey, 0)

    fun getLastReceivedTimestamp(storage: KeyValueStorage, source: MessageSource.File): Long {
        return storage.getLong(getLastReceivedTimestampKey(source), getBaseline(storage))
    }

    fun setLastReceivedTimestamp(storage: KeyValueStorage, source: MessageSource.File, timestampInMillis: Long) {
        storage.putLong(getLastReceivedTimestampKey(source), timestampInMillis)
    }

    fun getLastReceivedTimestampKey(source: MessageSource.File): String {
        return "${lastReceivedTimestampMillisKey}_${source.serialize()}"
    }

}
