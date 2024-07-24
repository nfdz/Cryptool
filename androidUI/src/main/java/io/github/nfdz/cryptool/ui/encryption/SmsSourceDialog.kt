package io.github.nfdz.cryptool.ui.encryption

import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.extension.hasPermission
import io.github.nfdz.cryptool.shared.platform.sms.smsPermissions
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.enforceSingleLine
import io.github.nfdz.cryptool.ui.extension.navigateToAppSystemSettings

@Composable
internal fun SmsSourceDialog(callback: (MessageSource.Sms?) -> Unit) {
    var explainPermissionDialog by remember { mutableStateOf(false) }
    var phoneAfterPermission by remember { mutableStateOf("") }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { output ->
        val isGranted = output.values.all { it }
        if (isGranted) {
            callback(MessageSource.Sms(phoneAfterPermission))
        } else {
            explainPermissionDialog = true
        }
    }

    if (explainPermissionDialog) {
        SmsExplainPermissionDialog(onDismiss = {
            explainPermissionDialog = false
            callback(null)
        })
    }

    Dialog(
        onDismissRequest = { callback(null) },
        content = {
            SmsSourceDialogContent {
                if (it == null) {
                    callback(null)
                } else {
                    val clearPhone = it.replace(Regex("[\\[\\]), -.]"), "")
                    if (context.hasPermission(smsPermissions)) {
                        callback(MessageSource.Sms(clearPhone))
                    } else {
                        phoneAfterPermission = clearPhone
                        launcher.launch(smsPermissions.toTypedArray())
                    }
                }
            }
        }
    )
}

@Composable
@Preview
private fun SmsSourceDialogPreview() {
    AppTheme {
        SmsSourceDialogContent {}
    }
}

@Composable
@Preview
private fun SmsExplainPermissionDialogPreview() {
    AppTheme {
        SmsExplainPermissionDialog {}
    }
}

private const val maxLength = 100

@Composable
internal fun SmsSourceDialogContent(callback: (String?) -> Unit) {
    var phone by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.medium),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                stringResource(R.string.encryption_source_sms_dialog_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.size(8.dp))
            PhoneTextField(
                value = phone,
                onValueChange = {
                    if (phone.length < maxLength) phone = it.enforceSingleLine()
                },
                onDone = {
                    if (validPhone(phone)) {
                        callback(phone)
                    }
                }
            )
            Spacer(Modifier.size(8.dp))
            Button(modifier = Modifier
                .fillMaxWidth()
                .size(40.dp),
                enabled = validPhone(phone),
                onClick = { callback(phone) }) {
                Text(stringResource(R.string.encryption_source_sms_dialog_set))
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

@Composable
private fun PhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.encryption_source_sms_dialog_phone)) },
    )
}

private fun validPhone(number: String): Boolean {
    return Patterns.PHONE.matcher(number).matches()
}

@Composable
private fun SmsExplainPermissionDialog(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                context.navigateToAppSystemSettings()
            }) { Text(stringResource(R.string.encryption_permission_sms_dialog_settings)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
        title = { Text(stringResource(R.string.encryption_permission_sms_dialog_title)) },
        text = { Text(stringResource(R.string.encryption_permission_sms_dialog_content)) },
    )
}