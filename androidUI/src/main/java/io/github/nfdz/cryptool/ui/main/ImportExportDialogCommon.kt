package io.github.nfdz.cryptool.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.core.import.ImportConfiguration
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.enforceSingleLine

internal object ImportExportDialogCommon {

    private const val maxLength = 1000

    data class DialogData(
        val code: String,
        val encryptions: Boolean,
        val messages: Boolean,
        val passwords: Boolean,
    ) {
        fun toConfiguration() = ImportConfiguration(
            encryptions = encryptions,
            messages = messages,
            passwords = passwords,
        )
    }

    @Composable
    fun DialogContent(
        title: String,
        action: String,
        onAction: (DialogData) -> Unit,
        onDismissDialog: () -> Unit,
    ) {
        var code by remember { mutableStateOf("") }
        var encryptions by remember { mutableStateOf(true) }
        var messages by remember { mutableStateOf(true) }
        var passwords by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.medium),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.size(8.dp))

                CheckboxEntry(
                    label = stringResource(R.string.main_import_dialog_encryptions),
                    checked = encryptions,
                    onCheckedChange = { encryptions = it }
                )

                CheckboxEntry(
                    label = stringResource(R.string.main_import_dialog_messages),
                    checked = messages,
                    onCheckedChange = { messages = it }
                )

                CheckboxEntry(
                    label = stringResource(R.string.main_import_dialog_passwords),
                    checked = passwords,
                    onCheckedChange = { passwords = it }
                )

                Spacer(Modifier.size(8.dp))
                Divider()
                Spacer(Modifier.size(8.dp))

                CodeTextField(
                    value = code,
                    onValueChange = {
                        if (code.length < maxLength) code = it.enforceSingleLine()
                    },
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp),
                    text = stringResource(R.string.main_import_dialog_code_caption),
                    style = MaterialTheme.typography.labelMedium,
                )

                Spacer(Modifier.size(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp),
                    onClick = {
                        onAction(
                            DialogData(
                                code = code,
                                encryptions = encryptions,
                                messages = messages,
                                passwords = passwords,
                            )
                        )
                    },
                    enabled = encryptions || messages || passwords,
                ) {
                    Text(action)
                }
                Spacer(Modifier.size(8.dp))
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp),
                    onClick = onDismissDialog,
                ) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        }
    }

    @Composable
    private fun CheckboxEntry(
        label: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
            Text(label)
        }
    }

    @Composable
    private fun CodeTextField(
        value: String,
        onValueChange: (String) -> Unit,
    ) {
        var codeVisibility by remember { mutableStateOf(false) }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (codeVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.main_import_dialog_code_hint)) },
            trailingIcon = {
                IconButton(onClick = {
                    codeVisibility = !codeVisibility
                }) {
                    if (codeVisibility) {
                        Icon(
                            painter = painterResource(R.drawable.ic_visibility_off),
                            stringResource(R.string.main_import_dialog_hide_code_icon_description)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_visibility),
                            stringResource(R.string.main_import_dialog_show_code_icon_description)
                        )
                    }
                }
            }
        )
    }

}