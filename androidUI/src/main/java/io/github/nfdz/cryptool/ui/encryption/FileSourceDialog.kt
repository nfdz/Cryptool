package io.github.nfdz.cryptool.ui.encryption

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R

@Composable
internal fun FileSourceDialog(callback: (MessageSource.File?) -> Unit) {
    Dialog(onDismissRequest = { callback(null) }, content = {
        FileSourceDialogContent {
            callback(it)
        }
    })
}

@Composable
@Preview
private fun FileSourceDialogPreview() {
    AppTheme {
        FileSourceDialogContent {}
    }
}

@Composable
internal fun FileSourceDialogContent(callback: (MessageSource.File?) -> Unit) {
    var inputFilePath by remember { mutableStateOf("") }
    var outputFilePath by remember { mutableStateOf("") }

    val context = LocalContext.current
    val launcherPickOutputFile = rememberLauncherForActivityResult(CreateFileContract()) { output ->
        output?.persistAccess(context)
        outputFilePath = output?.toString() ?: ""
    }
    val launcherPickInputFile = rememberLauncherForActivityResult(CreateFileContract()) { input ->
        input?.persistAccess(context)
        inputFilePath = input?.toString() ?: ""
    }

    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.medium),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                stringResource(R.string.encryption_source_file_dialog_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.size(8.dp))
            TextButton(modifier = Modifier
                .fillMaxWidth(),
                enabled = inputFilePath.isBlank(),
                onClick = {
                    launcherPickInputFile.launch("")
                }) {
                Text(stringResource(R.string.encryption_source_file_dialog_input))
            }
            Spacer(Modifier.size(8.dp))
            TextButton(modifier = Modifier
                .fillMaxWidth(),
                enabled = outputFilePath.isBlank(),
                onClick = {
                    launcherPickOutputFile.launch("")
                }) {
                Text(stringResource(R.string.encryption_source_file_dialog_output))
            }
            Spacer(Modifier.size(8.dp))
            Button(modifier = Modifier
                .fillMaxWidth()
                .size(40.dp),
                enabled = inputFilePath.isNotBlank() && outputFilePath.isNotBlank(),
                onClick = {
                    callback(
                        MessageSource.File(
                            inputFilePath = inputFilePath,
                            outputFilePath = outputFilePath,
                        )
                    )
                }) {
                Text(stringResource(R.string.encryption_source_file_dialog_set))
            }
            Spacer(Modifier.size(8.dp))
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(40.dp),
                onClick = { callback(null) },
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    }
}

private fun Uri.persistAccess(context: Context) {
    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    context.contentResolver.takePersistableUriPermission(this, takeFlags)
}

private const val mimeType = "*/*"

private class CreateFileContract : ActivityResultContracts.CreateDocument(mimeType) {
    override fun createIntent(context: Context, input: String): Intent {
        return super.createIntent(context, input).also {
            it.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
            )
        }
    }
}
