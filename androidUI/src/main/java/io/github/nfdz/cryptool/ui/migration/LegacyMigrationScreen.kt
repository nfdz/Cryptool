package io.github.nfdz.cryptool.ui.migration

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationInformation
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.EmptyGatekeeperViewModel
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperAction
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.ui.AppMessagesEffect
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.LargeAppIcon
import io.github.nfdz.cryptool.ui.common.LargeAppName
import io.github.nfdz.cryptool.ui.extension.getTutorialInformation
import io.github.nfdz.cryptool.ui.platform.EmptyLegacyPinCodeManager
import io.github.nfdz.cryptool.ui.platform.LegacyPinCodeManager

@Composable
@Preview
private fun LegacyMigrationScreenPreview() {
    AppTheme {
        LegacyMigrationScreenContent(
            snackbar = SnackbarHostState(),
            viewModel = EmptyGatekeeperViewModel,
            legacyPinCodeManager = EmptyLegacyPinCodeManager,
            activity = null,
            legacyMigrationInfo = LegacyMigrationInformation(true),
        )
    }
}

@Composable
internal fun LegacyMigrationScreen(
    viewModel: GatekeeperViewModel,
    activity: FragmentActivity?,
    legacyMigrationInfo: LegacyMigrationInformation,
) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    LegacyMigrationScreenContent(
        snackbar = snackbar,
        legacyPinCodeManager = EmptyLegacyPinCodeManager,
        viewModel = viewModel,
        activity = activity,
        legacyMigrationInfo = legacyMigrationInfo,
    )
}

@Composable
internal fun LegacyMigrationScreenContent(
    snackbar: SnackbarHostState,
    viewModel: GatekeeperViewModel,
    legacyPinCodeManager: LegacyPinCodeManager,
    activity: FragmentActivity?,
    legacyMigrationInfo: LegacyMigrationInformation,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                LargeAppIcon()
                LargeAppName()
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    stringResource(R.string.legacy_migration_topbar_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.legacy_migration_description),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.legacy_migration_section),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.legacy_migration_migration_details),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(onClick = {
                        launchMigration(viewModel, legacyPinCodeManager, activity, legacyMigrationInfo.hasCode)
                    }) {
                        Text(stringResource(R.string.legacy_migration_action))
                    }
                }
                Spacer(modifier = Modifier.height(90.dp))
            }
        },
    )
}

private fun launchMigration(
    viewModel: GatekeeperViewModel,
    legacyPinCodeManager: LegacyPinCodeManager,
    activity: FragmentActivity?,
    hasCode: Boolean
) {
    if (hasCode) {
        legacyPinCodeManager.askCode(
            activity = activity!!,
            onSuccessListener = {
                viewModel.acknowledgeLegacyMigration(context = activity, migrateData = true)
            },
            onDeleteListener = {
                viewModel.acknowledgeLegacyMigration(context = activity, migrateData = false)
            },
        )
    } else {
        viewModel.acknowledgeLegacyMigration(context = activity!!, migrateData = true)
    }
}

private fun GatekeeperViewModel.acknowledgeLegacyMigration(context: Context, migrateData: Boolean) {
    val tutorial = context.getTutorialInformation()
    dispatch(GatekeeperAction.AcknowledgeLegacyMigration(welcomeTutorial = tutorial, migrateData = migrateData))
}