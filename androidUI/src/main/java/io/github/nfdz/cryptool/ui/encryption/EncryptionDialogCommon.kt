package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.minPasswordLength
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.enforceSingleLine

internal object EncryptionDialogCommon {

    private const val maxLength = 1000

    data class DialogData(
        val name: String,
        val password: String,
        val algorithm: AlgorithmVersion,
    )

    @Composable
    fun DialogContent(
        title: String,
        action: String,
        initialValues: DialogData?,
        onDismissDialog: () -> Unit,
        isValid: (DialogData) -> Boolean,
        onClick: (DialogData) -> Unit,
    ) {
        var name by remember { mutableStateOf(initialValues?.name ?: "") }
        var password by remember { mutableStateOf(initialValues?.password ?: "") }
        var algorithm by remember { mutableStateOf(initialValues?.algorithm ?: AlgorithmVersion.values().last()) }

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
                NameTextField(
                    value = name,
                    onValueChange = {
                        if (name.length < maxLength) name = it.enforceSingleLine()
                    },
                )
                Spacer(Modifier.size(8.dp))
                PasswordTextField(value = password, onValueChange = {
                    if (password.length < maxLength) password = it.enforceSingleLine()
                }, onDone = { onClick(DialogData(name, password, algorithm)) })
                Text(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    text = stringResource(R.string.input_minimum_length, minPasswordLength),
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.size(8.dp))
                AlgorithmPicker(algorithm) {
                    algorithm = it
                }
                Spacer(Modifier.size(8.dp))
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .size(40.dp),
                    enabled = isValid(DialogData(name, password, algorithm)),
                    onClick = { onClick(DialogData(name, password, algorithm)) }) {
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
    private fun NameTextField(
        value: String,
        onValueChange: (String) -> Unit,
    ) {
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.encryption_name_hint)) })
    }

    @Composable
    private fun PasswordTextField(
        value: String, onValueChange: (String) -> Unit, onDone: (() -> Unit)
    ) {
        var passwordVisibility by remember { mutableStateOf(false) }

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.encryption_password_hint)) },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    if (passwordVisibility) {
                        Icon(
                            painter = painterResource(R.drawable.ic_visibility_off),
                            stringResource(R.string.encryption_hide_password_icon_description)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_visibility),
                            stringResource(R.string.encryption_show_password_icon_description)
                        )
                    }
                }
            })
    }

    @Composable
    private fun AlgorithmPicker(algorithm: AlgorithmVersion, onSelectAlgorithm: (AlgorithmVersion) -> Unit) {
        var expanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = true })
                .padding(
                    vertical = 4.dp,
                    horizontal = 2.dp,
                )
        ) {
            Text(stringResource(R.string.encryption_algorithm_label), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.size(8.dp))
            Text(algorithm.description, style = MaterialTheme.typography.bodyMedium)

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                AlgorithmVersion.values().forEach {
                    DropdownMenuItem(
                        text = { Text(it.description) },
                        onClick = {
                            onSelectAlgorithm(it)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}