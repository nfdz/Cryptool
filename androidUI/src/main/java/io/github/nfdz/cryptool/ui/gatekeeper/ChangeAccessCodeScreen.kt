package io.github.nfdz.cryptool.ui.gatekeeper

import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon
import io.github.nfdz.cryptool.ui.extension.enforceSingleLine
import org.koin.core.context.GlobalContext

@Composable
internal fun ChangeAccessCodeScreen(router: Router, viewModel: GatekeeperViewModel = GlobalContext.get().get()) {
    AutoCloseEffect(router, viewModel)

    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)

    val state = viewModel.observeState().collectAsState().value

    ChangeAccessCodeScreenContent(
        snackbar = snackbar,
        viewModel = viewModel,
        router = router,
        state = state,
    )
}

@Composable
@Preview
private fun ChangeAccessCodePreview() {
    AppTheme {
        ChangeAccessCodeScreenContent(
            snackbar = SnackbarHostState(),
            viewModel = EmptyGatekeeperViewModel,
            router = EmptyRouter,
            state = GatekeeperState(
                isOpen = true,
                hasCode = true,
                welcome = null,
                canUseBiometricAccess = true,
                canMigrateFromLegacy = null,
                loadingAccess = false,
            )
        )
    }
}

private const val maxLength = 1000

@Composable
internal fun ChangeAccessCodeScreenContent(
    snackbar: SnackbarHostState,
    viewModel: GatekeeperViewModel,
    router: Router,
    state: GatekeeperState,
) {
    var oldAccessCode by remember { mutableStateOf("") }
    var newAccessCode by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    if (router.supportAdvancedFeatures()) {
        BackHandler(state.loadingAccess) {
            // block back button
        }
    }

    val handleChangeCode = {
        viewModel.dispatch(
            GatekeeperAction.ChangeAccessCode(
                oldCode = oldAccessCode,
                newCode = newAccessCode,
            )
        )
        focusManager.clearFocus()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBarCommon(stringResource(R.string.change_access_topbar_title), router)
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PaddingValues(28.dp)),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Spacer(modifier = Modifier.weight(0.4f))
                    Text(stringResource(R.string.change_access_description))
                    Spacer(modifier = Modifier.size(8.dp))
                    CodeTextField(
                        value = oldAccessCode,
                        onValueChange = {
                            if (oldAccessCode.length < maxLength) oldAccessCode = it.enforceSingleLine()
                        },
                        label = stringResource(R.string.change_access_old_code_hint),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                        ),
                        enabled = !state.loadingAccess,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    CodeTextField(
                        value = newAccessCode,
                        onValueChange = {
                            if (newAccessCode.length < maxLength) newAccessCode = it.enforceSingleLine()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                        ),
                        label = stringResource(R.string.change_access_new_code_hint),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        enabled = !state.loadingAccess,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = handleChangeCode,
                        enabled = !state.loadingAccess && isCodeValid(oldAccessCode) && isCodeValid(newAccessCode),
                    ) {
                        if (state.loadingAccess) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(stringResource(R.string.change_access_change_action))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
    )
}

@Composable
private fun CodeTextField(
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var codeVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (codeVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        value = value,
        label = { Text(label) },
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
private fun AutoCloseEffect(router: Router, viewModel: GatekeeperViewModel) {
    val effect = viewModel.observeSideEffect().collectAsState(null).value ?: return
    LaunchedEffect(effect) {
        if (effect is GatekeeperEffect.ChangedCode) {
            router.popBackStack()
        }
    }
}