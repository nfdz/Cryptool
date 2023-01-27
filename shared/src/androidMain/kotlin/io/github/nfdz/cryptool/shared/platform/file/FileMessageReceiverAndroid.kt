package io.github.nfdz.cryptool.shared.platform.file

import android.content.ContentResolver
import android.content.Context
import androidx.core.net.toUri
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import kotlinx.coroutines.*
import java.io.FileInputStream

class FileMessageReceiverAndroid(
    private val context: Context,
    private val encryptionRepository: EncryptionRepository,
    private val messageRepository: MessageRepository,
    private val keyValueStorage: KeyValueStorage,
) : FileMessageReceiver, CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        private const val tag = "FileMessageReceiver"
        private const val lastReceivedBaselineMillisKey = "file_last_received_baseline"
        private const val lastReceivedTimestampMillisKey = "file_last_received_timestamp"
        private const val pollingPeriodInMillis = 30_000L
    }

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    private var pullFilesJob: Job? = null

    override fun launchMessagesPolling(isOpen: () -> Boolean) {
        pullFilesJob?.cancel()
        pullFilesJob = launch {
            val jobId = pullFilesJob?.hashCode().toString()
            while (true) {
                if (isOpen()) {
                    Napier.d(tag = tag, message = "[$jobId] Polling file messages...")
                    fetchMessages()
                    delay(pollingPeriodInMillis)
                }
            }
        }
    }

    override fun afterReset() {
        pullFilesJob?.cancel()
        // Set this baseline to avoid capturing old file entries
        keyValueStorage.putLong(lastReceivedBaselineMillisKey, System.currentTimeMillis())
    }

    private fun getBaselineInMillis(): Long = keyValueStorage.getLong(lastReceivedBaselineMillisKey, 0)

    private suspend fun fetchMessages() {
        val encryptions = encryptionRepository.getAllWith(MessageSource.filePrefix)
        encryptions.forEach { encryption ->
            runCatching {
                val result = getMessagesFor(encryption).sortedBy { it.timestampInMillis }
                if (result.isNotEmpty()) {
                    result.forEach { data ->
                        receiveMessageInternal(encryption, data)
                    }
                    val lastEntry = result.last()
                    lastEntry.source.setLastReceivedTimestamp(lastEntry.timestampInMillis)
                }
            }.onFailure {
                Napier.e(tag = tag, message = "Error receiving pending file message", throwable = it)
            }
        }
    }

    private suspend fun receiveMessageInternal(encryption: Encryption, data: MessageToReceive) {
        Napier.d(tag = tag, message = "Message from '${encryption.name}: '${data.encryptedMessage}")
        val cryptography = encryption.algorithm.createCryptography()
        val message = cryptography.decrypt(encryption.password, data.encryptedMessage)
            ?: return Napier.d(tag = tag, message = "Cannot process the message")
        messageRepository.receiveMessageAsync(
            encryptionId = encryption.id,
            message = message,
            encryptedMessage = data.encryptedMessage,
            timestampInMillis = data.timestampInMillis,
        )
    }

    private fun getMessagesFor(encryption: Encryption): List<MessageToReceive> {
        Napier.d(tag = tag, message = "Get messages for '${encryption.name}'")
        val result = mutableListOf<MessageToReceive>()
        val source = encryption.source as MessageSource.File
        val uri = source.inputFilePath.toUri()
        val lastReceivedTimestamp = source.getLastReceivedTimestamp()

        contentResolver.openFileDescriptor(uri, "r")?.use {
            FileInputStream(it.fileDescriptor).use { input ->
                input.bufferedReader().use { br ->
                    br.readLines().forEach { line ->
                        runCatching {
                            val parts = line.split(",")
                            val timestampInMillis = parts[0].toLong()
                            if (timestampInMillis > lastReceivedTimestamp) {
                                result.add(MessageToReceive(source, parts[1], timestampInMillis))
                            }
                        }.onFailure {
                            Napier.e(tag = tag, message = "Get messages for '${encryption.name}' - Invalid line: $line")
                        }
                    }
                }
            }
        }

        return result
    }

    private fun MessageSource.File.getLastReceivedTimestamp(): Long {
        return keyValueStorage.getLong(getLastReceivedTimestampKey(), getBaselineInMillis())
    }

    private fun MessageSource.File.setLastReceivedTimestamp(value: Long) {
        keyValueStorage.putLong(getLastReceivedTimestampKey(), value)
    }

    private fun MessageSource.File.getLastReceivedTimestampKey(): String {
        return "${lastReceivedTimestampMillisKey}_${serialize()}"
    }
}

private class MessageToReceive(
    val source: MessageSource.File,
    val encryptedMessage: String,
    val timestampInMillis: Long
)