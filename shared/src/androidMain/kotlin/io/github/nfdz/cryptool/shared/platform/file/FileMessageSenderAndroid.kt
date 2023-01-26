package io.github.nfdz.cryptool.shared.platform.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
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

        val uri = outputFilePath.toUri()
//        val output = context.contentResolver.openOutputStream(uri) ?: throw IllegalStateException("Cannot open file")
//        output.use {
//            it.bufferedWriter().use { bw ->
//                bw
//                bw.write("$timestampInMillis,$value\n")
//            }
//        }

        contentResolver.openFileDescriptor(uri, "wa")?.use {
            FileOutputStream(it.fileDescriptor).use { output ->
                output.bufferedWriter().use { bw ->
                    bw.write("$timestampInMillis,$value\n")
                }
            }
        }

        //TODO throw controlled exception
    }

    private fun alterDocument(uri: Uri) {
//        try {
//
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }

}