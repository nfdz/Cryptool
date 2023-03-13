package io.github.nfdz.cryptool.ui.gatekeeper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.EmptyGatekeeperViewModel
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperAction
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperState
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon
import org.koin.core.context.GlobalContext

@Composable
internal fun ChangeBiometricAccessScreen(
    router: Router,
    viewModel: GatekeeperViewModel = GlobalContext.get().get()
) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)

    val state = viewModel.observeState().collectAsState().value

    ChangeBiometricAccessScreenContent(
        snackbar = snackbar,
        viewModel = viewModel,
        router = router,
        state = state,
    )
}


@Composable
@Preview
private fun ChangeBiometricAccessScreenPreview() {
    AppTheme {
        ChangeBiometricAccessScreenContent(
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


@Composable
internal fun ChangeBiometricAccessScreenContent(
    snackbar: SnackbarHostState,
    viewModel: GatekeeperViewModel,
    router: Router,
    state: GatekeeperState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBarCommon(stringResource(R.string.change_biometric_topbar_title), router)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.gatekeeper_enable_biometrics),
                            modifier = Modifier.padding(bottom = 2.dp, end = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Switch(
                            checked = state.canUseBiometricAccess,
                            onCheckedChange = {
                                viewModel.dispatch(
                                    GatekeeperAction.ChangeBiometricAccess(
                                        biometricEnabled = it,
                                    )
                                )
                            },
                        )
                    }
                }
            }
        },
    )
}
