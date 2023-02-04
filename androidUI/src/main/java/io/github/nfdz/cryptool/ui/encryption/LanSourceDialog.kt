package io.github.nfdz.cryptool.ui.encryption

import android.net.InetAddresses
import android.os.Build
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.platform.network.LanDiscovery
import io.github.nfdz.cryptool.shared.platform.network.LanReceiver
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.enforceSingleLine
import org.koin.core.context.GlobalContext

@Composable
internal fun LanSourceDialog(
    lanDiscovery: LanDiscovery = GlobalContext.get().get(),
    lanReceiver: LanReceiver = GlobalContext.get().get(),
    callback: (MessageSource.Lan?) -> Unit
) {
    val networkAddresses = lanDiscovery.observeAddresses().collectAsState(initial = emptyList()).value
    Dialog(onDismissRequest = { callback(null) }, content = {
        LanSourceDialogContent(networkAddresses, lanReceiver.getPort(), lanReceiver.getFreeSlot()) {
            callback(it)
        }
    })
}

@Composable
internal fun LanSourceEffect() {
    LaunchedEffect(true) {
        runCatching {
            val lanDiscovery: LanDiscovery = GlobalContext.get().get()
            lanDiscovery.setupNetworkCallback()
        }
    }
}

@Composable
@Preview
private fun LanSourceDialogPreview() {
    AppTheme {
        LanSourceDialogContent(listOf("0.0.0.0"), 123, 12) {}
    }
}

private const val maxLength = 100

@Composable
internal fun LanSourceDialogContent(
    networkAddresses: List<String>,
    serverPort: Int,
    serverSlot: Int,
    callback: (MessageSource.Lan?) -> Unit
) {
    var address by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var slot by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.medium),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(R.string.encryption_source_lan_dialog_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.size(8.dp))
            Divider()
            Spacer(Modifier.size(8.dp))
            Text(
                stringResource(R.string.encryption_source_lan_dialog_you_section),
                style = MaterialTheme.typography.titleSmall,
            )
            Row() {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    networkAddresses.forEach {
                        SelectionContainer() { Text(it, style = MaterialTheme.typography.labelMedium) }
                    }
                    if (networkAddresses.isEmpty()) {
                        Text("-", style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(Modifier.size(8.dp))
                Box(
                    Modifier
                        .height(20.dp)
                        .width(DividerDefaults.Thickness)
                        .background(color = DividerDefaults.color)
                )
                Spacer(Modifier.size(8.dp))
                SelectionContainer(Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(R.string.encryption_source_lan_dialog_you_port, serverPort),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            stringResource(R.string.encryption_source_lan_dialog_you_slot, serverSlot),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            Spacer(Modifier.size(8.dp))
            Divider()
            Spacer(Modifier.size(8.dp))
            Text(
                stringResource(R.string.encryption_source_lan_dialog_other_section),
                style = MaterialTheme.typography.titleSmall,
            )
            AddressTextField(
                value = address,
                isValid = validAddress(address),
                onValueChange = {
                    if (address.length < maxLength) address = it.enforceSingleLine()
                },
            )
            PortTextField(
                value = port,
                onValueChange = {
                    if (port.length < maxLength) port = it.enforceSingleLine()
                },
            )
            SlotTextField(
                value = slot,
                onValueChange = {
                    if (slot.length < maxLength) slot = it.enforceSingleLine()
                },
                onDone = {
                    if (isValid(address, port, slot)) {
                        callback(MessageSource.Lan(address, port, slot))
                    }
                }
            )
            Spacer(Modifier.size(8.dp))
            Button(modifier = Modifier
                .fillMaxWidth()
                .size(40.dp),
                enabled = isValid(address, port, slot),
                onClick = { callback(MessageSource.Lan(address, port, slot)) }) {
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
private fun AddressTextField(
    value: String,
    isValid: Boolean,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
        ),
        isError = value.isNotEmpty() && !isValid,
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.encryption_source_lan_dialog_other_address)) },
    )
}

@Composable
private fun PortTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
        ),
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.encryption_source_lan_dialog_other_port)) },
    )
}

@Composable
private fun SlotTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.encryption_source_lan_dialog_other_slot)) },
    )
}

private fun isValid(address: String, port: String, slot: String): Boolean {
    return validAddress(address) && validNumber(port) && validNumber(slot)
}

private fun validNumber(value: String): Boolean {
    val valueInt = value.toIntOrNull()
    return valueInt != null && valueInt > 0
}

private fun validAddress(value: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        InetAddresses.isNumericAddress(value)
    } else {
        @Suppress("DEPRECATION") Patterns.IP_ADDRESS.matcher(value).matches()
    }
}