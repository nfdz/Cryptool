package io.github.nfdz.cryptool.ui.welcome

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.gatekeeper.entity.WelcomeInformation
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.EmptyGatekeeperViewModel
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperAction
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.ui.AppMessagesEffect
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.LargeAppIcon
import io.github.nfdz.cryptool.ui.common.LargeAppName
import io.github.nfdz.cryptool.ui.extension.getTutorialInformation

@Composable
@Preview
private fun WelcomeScreenPreview() {
    AppTheme {
        WelcomeScreenContent(
            snackbar = SnackbarHostState(),
            viewModel = EmptyGatekeeperViewModel,
            information = WelcomeInformation(
                title = stringResource(R.string.app_slogan),
                content = stringResource(R.string.welcome_main_description),
                welcomeTutorial = true,
            ),
        )
    }
}

@Composable
internal fun WelcomeScreen(
    viewModel: GatekeeperViewModel,
    information: WelcomeInformation,
) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    WelcomeScreenContent(snackbar, viewModel, information)
}

@Composable
internal fun WelcomeScreenContent(
    snackbar: SnackbarHostState,
    viewModel: GatekeeperViewModel,
    information: WelcomeInformation,
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.acknowledgeWelcome(context, information.welcomeTutorial)
            }) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = stringResource(android.R.string.ok),
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                LargeAppIcon()
                LargeAppName()
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    information.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    information.content,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(90.dp))
            }
        },
    )
}

private fun GatekeeperViewModel.acknowledgeWelcome(context: Context, welcomeTutorial: Boolean) {
    val tutorial = if (welcomeTutorial) {
        context.getTutorialInformation()
    } else {
        null
    }
    dispatch(GatekeeperAction.AcknowledgeWelcome(tutorial))
}