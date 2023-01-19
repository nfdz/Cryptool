package io.github.nfdz.cryptool.ui.password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.core.password.PasswordGenerator
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.enforceSingleLine
import kotlinx.coroutines.launch

internal object PasswordDialogCommon {

    private const val maxLength = 1000

    data class DialogData(
        val name: String,
        val password: String,
        val tags: String,
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
        var tags by remember { mutableStateOf(initialValues?.tags ?: "") }

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
                    onValueChange = { if (name.length < maxLength) name = it.enforceSingleLine() },
                )
                Spacer(Modifier.size(8.dp))
                PasswordTextField(
                    value = password,
                    onValueChange = { if (password.length < maxLength) password = it.enforceSingleLine() },
                )
                Spacer(Modifier.size(8.dp))
                TagsTextField(value = tags,
                    onValueChange = { if (tags.length < maxLength) tags = it.enforceSingleLine() },
                    onDone = { onClick(DialogData(name, password, tags)) })
                Spacer(Modifier.size(16.dp))
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .size(40.dp),
                    enabled = isValid(DialogData(name, password, tags)),
                    onClick = { onClick(DialogData(name, password, tags)) }) {
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
            label = { Text(stringResource(R.string.password_name_hint)) })
    }

    @Composable
    private fun TagsTextField(
        value: String,
        onValueChange: (String) -> Unit,
        onDone: () -> Unit,
    ) {
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.password_tags_hint)) })
    }

    @Composable
    private fun PasswordTextField(
        value: String,
        onValueChange: (String) -> Unit,
    ) {
        val scope = rememberCoroutineScope()
        var passwordVisibility by remember { mutableStateOf(false) }

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
            ),
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.password_password_hint)) },
            trailingIcon = {
                Row {
                    IconButton(onClick = {
                        scope.launch {
                            onValueChange(PasswordGenerator.generate())
                        }
                    }) {
                        Icon(Icons.Filled.ShuffleOn, stringResource(R.string.password_random_icon_description))
                    }
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        if (passwordVisibility) {
                            Icon(
                                painter = painterResource(R.drawable.ic_visibility_off),
                                stringResource(R.string.password_hide_icon_description)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_visibility),
                                stringResource(R.string.password_show_icon_description)
                            )
                        }
                    }
                }
            })
    }
}