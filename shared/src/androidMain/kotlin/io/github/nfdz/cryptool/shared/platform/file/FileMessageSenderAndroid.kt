package io.github.nfdz.cryptool.shared.platform.file

import android.content.ContentResolver
import android.content.Context
import androidx.core.net.toUri
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import java.io.FileOutputStream

class FileMessageSenderAndroid(
    private val context: Context,
    messageRepository: MessageRepository,
) : FileMessageSender {

    companion object {
        private const val tag = "FileMessageSender"
    }

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    init {
        messageRepository.addOnSendMessageAction { source, encryptedMessage ->
            val fileSource = source as? MessageSource.File ?: return@addOnSendMessageAction
            sendMessage(fileSource.outputFilePath, encryptedMessage)
        }
    }

    override fun sendMessage(outputFilePath: String, encryptedMessage: String) {
        Napier.d(tag = tag, message = "Send message to $outputFilePath: '$encryptedMessage'")
        val timestampInMillis = System.currentTimeMillis()
        runCatching {
            val uri = outputFilePath.toUri()
            contentResolver.openFileDescriptor(uri, "wa")?.use {
                FileOutputStream(it.fileDescriptor).use { output ->
                    output.bufferedWriter().use { bw ->
                        bw.write("$timestampInMillis,$encryptedMessage\n")
                    }
                }
            }
        }.onFailure {
            Napier.e(tag = tag, message = "Send message error", throwable = it)
            throw FileMessageSendException(it)
        }
    }

}
