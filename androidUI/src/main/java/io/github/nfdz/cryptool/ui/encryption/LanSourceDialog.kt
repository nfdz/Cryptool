package io.github.nfdz.cryptool.ui.encryption

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.InetAddresses
import android.os.Build
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.deserializeMessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.platform.network.LanDiscovery
import io.github.nfdz.cryptool.shared.platform.network.LanReceiver
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.enforceSingleLine
import io.github.nfdz.cryptool.ui.platform.QrProviderAndroid
import kotlinx.coroutines.launch
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
        LanSourceDialogContent(listOf("1.2.3.4"), 123, 12) {}
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
    val context = LocalContext.current
    var qrMode by remember { mutableStateOf(false) }
    val address = remember { mutableStateOf("") }
    val port = remember { mutableStateOf("") }
    val slot = remember { mutableStateOf("") }

    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.medium),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            Row {
                Text(
                    stringResource(R.string.encryption_source_lan_dialog_title),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                )
                if (networkAddresses.isNotEmpty()) {
                    IconButton(onClick = {
                        qrMode = !qrMode
                    }) {
                        if (qrMode) {
                            Icon(
                                Icons.Rounded.EditNote,
                                contentDescription = stringResource(R.string.encryption_source_lan_dialog_manual_mode),
                            )
                        } else {
                            Icon(
                                Icons.Rounded.QrCode,
                                contentDescription = stringResource(R.string.encryption_source_lan_dialog_qr_mode),
                            )
                        }
                    }
                }
            }
            Text(
                stringResource(R.string.encryption_source_lan_dialog_you_section),
                style = MaterialTheme.typography.titleSmall,
            )
            if (qrMode) {
                QrModeServer(
                    networkAddresses = networkAddresses,
                    serverPort = serverPort,
                    serverSlot = serverSlot,
                )
            } else {
                ManualModeServer(
                    networkAddresses = networkAddresses,
                    serverPort = serverPort,
                    serverSlot = serverSlot,
                )
            }
            Spacer(Modifier.size(8.dp))
            Divider()
            Spacer(Modifier.size(8.dp))
            Text(
                stringResource(R.string.encryption_source_lan_dialog_other_section),
                style = MaterialTheme.typography.titleSmall,
            )
            if (qrMode && context.canScanQr()) {
                QrModeClient(address = address, port = port, slot = slot)
            } else {
                ManualModeClient(
                    address = address,
                    port = port,
                    slot = slot,
                    onDone = {
                        if (isValid(address.value, port.value, slot.value)) {
                            callback(MessageSource.Lan(address.value, port.value, slot.value))
                        }
                    },
                )
            }
            Spacer(Modifier.size(8.dp))
            Button(modifier = Modifier
                .fillMaxWidth()
                .size(40.dp),
                enabled = isValid(address.value, port.value, slot.value),
                onClick = { callback(MessageSource.Lan(address.value, port.value, slot.value)) }) {
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
private fun QrModeServer(
    networkAddresses: List<String>,
    serverPort: Int,
    serverSlot: Int,
) {
    var qrMap by remember { mutableStateOf<Map<String, Bitmap>>(emptyMap()) }
    val imageSize = 180.dp
    val imageSizeInPx = with(LocalDensity.current) { imageSize.toPx().toInt() }

    LaunchedEffect(true) {
        val data = joinData(networkAddresses, serverPort.toString(), serverSlot.toString())
        qrMap = data.mapNotNull {
            val result = QrProviderAndroid().encode(it.serialize(), imageSizeInPx)
            if (result == null) null else it.address to result
        }.associate { it }
    }

    Spacer(Modifier.size(18.dp))
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (qrMap.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            qrMap.forEach { qrEntry ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .padding(8.dp),
                ) {
                    Image(
                        modifier = Modifier.size(imageSize),
                        bitmap = qrEntry.value.asImageBitmap(),
                        contentDescription = qrEntry.key
                    )
                }
                Text(qrEntry.key, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun ManualModeServer(
    networkAddresses: List<String>,
    serverPort: Int,
    serverSlot: Int,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
}

@Composable
private fun QrModeClient(
    address: MutableState<String>,
    port: MutableState<String>,
    slot: MutableState<String>,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcherScanQr = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val extras = it.data?.extras
            val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras?.getParcelable("data", Bitmap::class.java)
            } else {
                @Suppress("DEPRECATION") extras?.getParcelable("data")
            }
            if (bitmap != null) {
                scope.launch {
                    val result = QrProviderAndroid().decode(bitmap)
                    val data = result?.splitData()
                    if (data != null) {
                        address.value = data.address
                        port.value = data.port
                        slot.value = data.slot
                    } else {
                        context.showScanQrErrorToast()
                    }
                }
            }
        }
    }
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            launcherScanQr.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }) {
        Text(stringResource(R.string.encryption_source_lan_dialog_qr_scan))
    }
}

private fun Context.showScanQrErrorToast() {
    Toast.makeText(this, R.string.encryption_source_lan_dialog_qr_scan_error, Toast.LENGTH_LONG).show()
}

private fun Context.canScanQr(): Boolean {
    return Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager) != null
}

@Composable
private fun ManualModeClient(
    address: MutableState<String>,
    port: MutableState<String>,
    slot: MutableState<String>,
    onDone: () -> Unit,
) {
    AddressTextField(
        value = address.value,
        isValid = validAddress(address.value),
        onValueChange = {
            if (address.value.length < maxLength) address.value = it.enforceSingleLine()
        },
    )
    PortTextField(
        value = port.value,
        onValueChange = {
            if (port.value.length < maxLength) port.value = it.enforceSingleLine()
        },
    )
    SlotTextField(
        value = slot.value,
        onValueChange = {
            if (slot.value.length < maxLength) slot.value = it.enforceSingleLine()
        },
        onDone = onDone,
    )
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

private fun joinData(
    networkAddresses: List<String>,
    serverPort: String,
    serverSlot: String,
): List<MessageSource.Lan> {
    return networkAddresses.map { address ->
        MessageSource.Lan(address, serverPort, serverSlot)
    }
}

private fun String.splitData(): MessageSource.Lan? {
    return runCatching {
        this.deserializeMessageSource() as? MessageSource.Lan
    }.getOrNull()
}