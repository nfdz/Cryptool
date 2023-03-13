package io.github.nfdz.cryptool.ui.gatekeeper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.*
import io.github.nfdz.cryptool.ui.AppMessagesEffect
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.enforceSingleLine
import io.github.nfdz.cryptool.ui.extension.supportBiometrics

@Composable
@Preview
private fun GatekeeperScreenAskPreview() {
    AppTheme {
        GatekeeperScreenContent(
            snackbar = SnackbarHostState(),
            supportAdvancedFeatures = true,
            activity = null,
            viewModel = EmptyGatekeeperViewModel,
            hasCode = true,
            canUseBiometricAccess = false,
            loadingAccess = false,
        )
    }
}

@Composable
@Preview
private fun GatekeeperScreenCreatePreview() {
    AppTheme {
        GatekeeperScreenContent(
            snackbar = SnackbarHostState(),
            supportAdvancedFeatures = true,
            activity = null,
            viewModel = EmptyGatekeeperViewModel,
            hasCode = false,
            canUseBiometricAccess = false,
            loadingAccess = false,
        )
    }
}

@Composable
fun GatekeeperScreen(
    activity: FragmentActivity?,
    viewModel: GatekeeperViewModel,
    hasCode: Boolean,
    canUseBiometricAccess: Boolean,
    loadingAccess: Boolean,
    supportAdvancedFeatures: Boolean,
) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    GatekeeperScreenContent(
        snackbar = snackbar,
        supportAdvancedFeatures = supportAdvancedFeatures,
        activity = activity,
        viewModel = viewModel,
        hasCode = hasCode,
        canUseBiometricAccess = canUseBiometricAccess,
        loadingAccess = loadingAccess,
    )
}

@Composable
internal fun GatekeeperScreenContent(
    snackbar: SnackbarHostState,
    activity: FragmentActivity?,
    viewModel: GatekeeperViewModel,
    hasCode: Boolean,
    canUseBiometricAccess: Boolean,
    loadingAccess: Boolean,
    supportAdvancedFeatures: Boolean,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        content = { padding ->
            if (hasCode) {
                AskCode(
                    Modifier.padding(padding),
                    activity = activity,
                    viewModel = viewModel,
                    canUseBiometricAccess = canUseBiometricAccess,
                    loadingAccess = loadingAccess,
                    supportAdvancedFeatures = supportAdvancedFeatures
                )
            } else {
                CreateCode(Modifier.padding(padding), activity, viewModel)
            }
        },
    )
}

private const val maxLength = 1000

@Composable
private fun AskCode(
    modifier: Modifier,
    activity: FragmentActivity?,
    viewModel: GatekeeperViewModel,
    canUseBiometricAccess: Boolean,
    loadingAccess: Boolean,
    supportAdvancedFeatures: Boolean,
) {
    var deleteDialogVisibility by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val handleInputCode = {
        viewModel.dispatch(GatekeeperAction.AccessWithCode(textInput))
        textInput = ""
        focusManager.clearFocus()
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.weight(2.0f))
        CodeTextField(
            value = textInput,
            onValueChange = {
                if (textInput.length < maxLength) textInput = it.enforceSingleLine()
            },
            onDone = handleInputCode,
            enabled = !loadingAccess,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = handleInputCode,
            enabled = !loadingAccess,
        ) {
            if (loadingAccess) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(stringResource(R.string.gatekeeper_open_action))
            }
        }
        Spacer(modifier = Modifier.weight(0.5f))
        if (activity != null && canUseBiometricAccess && activity.supportBiometrics()) {
            Box(
                Modifier.fillMaxWidth()
            ) {
                IconButton(
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.Center)
                        .padding(8.dp),
                    onClick = { viewModel.dispatch(GatekeeperAction.AccessWithBiometric(activity)) },
                    enabled = !loadingAccess
                ) {
                    Icon(
                        Icons.Rounded.Fingerprint,
                        stringResource(R.string.gatekeeper_open_biometrics_icon_description),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        } else {
            Spacer(modifier = Modifier.weight(0.8f))
        }
        if (supportAdvancedFeatures) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !loadingAccess,
                onClick = {
                    deleteDialogVisibility = true
                }) {
                Text(stringResource(R.string.gatekeeper_do_not_remember))
            }
        }
    }
    if (deleteDialogVisibility) {
        ConfirmDeleteDialog(onDismiss = {
            deleteDialogVisibility = false
        }, onConfirm = {
            deleteDialogVisibility = false
            viewModel.dispatch(GatekeeperAction.Delete)
        })
    }
}

@Composable
private fun CreateCode(
    modifier: Modifier,
    activity: FragmentActivity?,
    viewModel: GatekeeperViewModel,
) {
    var textInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var biometricEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.weight(1.2f))
        Text(stringResource(R.string.gatekeeper_set_code_label))
        Spacer(modifier = Modifier.size(8.dp))
        CodeTextField(
            value = textInput,
            onValueChange = {
                if (textInput.length < maxLength) textInput = it.enforceSingleLine()
            },
            onDone = { focusManager.clearFocus() },
        )
        Text(
            modifier = Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp),
            text = stringResource(R.string.input_minimum_length, minCodeLength),
            style = MaterialTheme.typography.labelMedium,
        )
        if (activity != null && activity.supportBiometrics()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.gatekeeper_enable_biometrics),
                    modifier = Modifier.padding(bottom = 2.dp, end = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Switch(checked = biometricEnabled, onCheckedChange = { biometricEnabled = it })
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = isCodeValid(textInput),
            onClick = {
                if (isCodeValid(textInput)) {
                    viewModel.dispatch(GatekeeperAction.Create(textInput, biometricEnabled))
                    focusManager.clearFocus()
                }
            },
        ) {
            Text(stringResource(R.string.gatekeeper_set_code_action))
        }
        Spacer(modifier = Modifier.weight(0.8f))
    }
}

@Composable
private fun CodeTextField(
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    onDone: (() -> Unit)
) {
    var codeVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (codeVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        value = value,
        enabled = enabled,
        singleLine = true,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = {
                codeVisibility = !codeVisibility
            }) {
                if (codeVisibility) {
                    Icon(
                        painter = painterResource(R.drawable.ic_visibility_off),
                        stringResource(R.string.gatekeeper_hide_icon_description)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_visibility),
                        stringResource(R.string.gatekeeper_show_icon_description)
                    )
                }
            }
        })
}

@Composable
private fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.gatekeeper_delete_code_dialog_action)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
        title = { Text(stringResource(R.string.gatekeeper_delete_code_dialog_title)) },
        text = { Text(stringResource(R.string.gatekeeper_delete_code_dialog_description)) },
    )
}