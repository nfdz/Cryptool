package io.github.nfdz.cryptool.shared.platform.file

import android.content.ContentResolver
import android.content.Context
import androidx.core.net.toUri
import io.github.aakira.napier.Napier
import java.io.FileOutputStream

class FileMessageSenderAndroid(private val context: Context) : FileMessageSender {

    companion object {
        private const val tag = "FileMessageSender"
    }

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    override fun sendMessage(outputFilePath: String, value: String) {
        Napier.d(tag = tag, message = "Send message to $outputFilePath: '$value'")
        val timestampInMillis = System.currentTimeMillis()
        runCatching {
            val uri = outputFilePath.toUri()
            contentResolver.openFileDescriptor(uri, "wa")?.use {
                FileOutputStream(it.fileDescriptor).use { output ->
                    output.bufferedWriter().use { bw ->
                        bw.write("$timestampInMillis,$value\n")
                    }
                }
            }
        }.onFailure {
            Napier.e(tag = tag, message = "Send message error", throwable = it)
            throw FileMessageSendException(it)
        }
    }

}
