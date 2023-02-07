package io.github.nfdz.cryptool.shared.platform.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.github.nfdz.cryptool.shared.platform.time.Clock
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream

class FileMessageReceiverAndroid(
    private val context: Context,
    private val encryptionRepository: EncryptionRepository,
    gatekeeperRepository: GatekeeperRepository,
    private val messageRepository: MessageRepository,
    private val storage: KeyValueStorage,
) : FileMessageReceiver, CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        private const val tag = "FileMessageReceiver"
        private const val pollingPeriodInMillis = 30_000L
        private const val maxAmountOfEntriesPerFile = 15
    }

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    private var pullFilesJob: Job? = null

    init {
        gatekeeperRepository.addOnOpenAction {
            if (isPollingNeeded()) {
                ensurePolling()
            }
        }
        gatekeeperRepository.addOnResetAction(::afterReset)
        encryptionRepository.addOnSetSourceAction { source ->
            if (source is MessageSource.File) {
                ensurePolling()
                FileMessageReceiverPreferences.setLastReceivedTimestamp(
                    storage,
                    source,
                    Clock.nowInMillis()
                )
            }
        }
    }

    private fun ensurePolling() {
        pullFilesJob?.cancel()
        pullFilesJob = launch {
            val jobId = pullFilesJob?.hashCode().toString()
            Napier.d(tag = tag, message = "[$jobId] Launch file messages polling")
            do {
                Napier.d(tag = tag, message = "[$jobId] Polling file messages...")
                fetchMessages()
                delay(pollingPeriodInMillis)
            } while (isPollingNeeded())
            Napier.d(tag = tag, message = "File messages polling is not needed anymore")
        }
    }

    private fun afterReset() {
        pullFilesJob?.cancel()
        FileMessageReceiverPreferences.setBaseline(storage)
    }

    private suspend fun fetchMessages() {
        val encryptions = encryptionRepository.getAllWith(MessageSource.filePrefix)
        encryptions.forEach { encryption ->
            runCatching {
                val result = getMessagesFor(encryption).sortedBy { it.timestampInMillis }
                if (result.isNotEmpty()) {
                    result.forEach { data ->
                        messageRepository.receiveMessageAsync(
                            encryption = encryption,
                            encryptedMessage = data.encryptedMessage,
                            timestampInMillis = data.timestampInMillis,
                        )
                    }
                    val lastEntry = result.last()
                    FileMessageReceiverPreferences.setLastReceivedTimestamp(
                        storage,
                        lastEntry.source,
                        lastEntry.timestampInMillis
                    )
                }
            }.onFailure {
                Napier.e(tag = tag, message = "Error receiving pending file message", throwable = it)
            }
        }
    }

    private fun isPollingNeeded(): Boolean = runCatching {
        return encryptionRepository.getAllWith(MessageSource.filePrefix).isNotEmpty()
    }.getOrElse { false }

    private fun getMessagesFor(encryption: Encryption): List<MessageToReceive> {
        Napier.d(tag = tag, message = "Getting messages from '${encryption.name}'")
        val result = mutableListOf<MessageToReceive>()
        val source = encryption.source as MessageSource.File
        val uri = source.inputFilePath.toUri()
        val lastReceivedTimestamp = FileMessageReceiverPreferences.getLastReceivedTimestamp(storage, source)
        var fileEntries = 0
        contentResolver.openFileDescriptor(uri, "r")?.use {
            FileInputStream(it.fileDescriptor).use { input ->
                input.bufferedReader().use { br ->
                    val lines = br.readLines()
                    fileEntries = lines.size
                    lines.forEach { line ->
                        runCatching {
                            val parts = line.split(",")
                            val timestampInMillis = parts[0].toLong()
                            if (timestampInMillis > lastReceivedTimestamp) {
                                result.add(MessageToReceive(source, parts[1], timestampInMillis))
                            }
                        }.onFailure {
                            Napier.e(
                                tag = tag,
                                message = "Getting messages from '${encryption.name}' - Invalid line: $line"
                            )
                        }
                    }
                }
            }
        }

        if (fileEntries > maxAmountOfEntriesPerFile) {
            Napier.d(tag = tag, message = "Clear file triggered. File entries: $fileEntries. File: '$uri'")
            clearFile(uri)
        }

        return result
    }

    private fun clearFile(uri: Uri) = runCatching {
        contentResolver.openFileDescriptor(uri, "wt")?.use {
            FileOutputStream(it.fileDescriptor).use { output ->
                output.write("".toByteArray())
            }
        }
    }.onFailure {
        Napier.e(tag = tag, message = "Clear file error", throwable = it)
    }
}

private class MessageToReceive(
    val source: MessageSource.File,
    val encryptedMessage: String,
    val timestampInMillis: Long
)