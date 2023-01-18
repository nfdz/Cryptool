package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoveToInbox
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R

@Composable
@Preview
private fun MessageInputPreview() {
    AppTheme {
        MessageInput(
            name = "Peter",
            source = MessageSource.MANUAL,
            onSendMessage = {},
            onReceiveMessage = {},
        )
    }
}

private const val maxLength = 8_000_000
private const val backgroundColorAlfa = 0.15f

@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    name: String,
    source: MessageSource,
    onSendMessage: (String) -> Unit,
    onReceiveMessage: (String) -> Unit,
) {
    Column(modifier = modifier) {
        if (source == MessageSource.MANUAL) {
            ReceiveMessageArea(name, onReceiveMessage)
        }
        SendMessageArea(onSendMessage)
    }
}

@Composable
private fun ReceiveMessageArea(name: String, onReceiveMessage: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val handleReceiveMessage = {
        onReceiveMessage(message)
        message = ""
        focusManager.clearFocus()
    }
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = backgroundColorAlfa)
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            placeholder = { Text(stringResource(R.string.encryption_receive_message_hint, name)) },
            value = message,
            onValueChange = { if (message.length < maxLength) message = it },
            keyboardActions = KeyboardActions(
                onSend = { handleReceiveMessage() }
            ),
            leadingIcon = {
                IconButton(
                    onClick = handleReceiveMessage,
                    enabled = message.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoveToInbox,
                        contentDescription = stringResource(R.string.encryption_receive_message_icon_description),
                    )
                }
            }
        )
    }
}

@Composable
private fun SendMessageArea(onSendMessage: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val handleSendMessage = {
        onSendMessage(message)
        message = ""
        focusManager.clearFocus()
    }
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.secondary.copy(alpha = backgroundColorAlfa)
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            placeholder = { Text(stringResource(R.string.encryption_send_message_hint)) },
            value = message,
            onValueChange = { if (message.length < maxLength) message = it },
            keyboardActions = KeyboardActions(
                onSend = { handleSendMessage() }
            ),
            trailingIcon = {
                IconButton(
                    onClick = handleSendMessage,
                    enabled = message.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Send,
                        contentDescription = stringResource(R.string.encryption_send_message_icon_description),
                    )
                }
            },
        )
    }
}
