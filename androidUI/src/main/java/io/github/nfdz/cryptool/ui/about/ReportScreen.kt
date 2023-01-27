package io.github.nfdz.cryptool.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.core.constant.AppUrl
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.IconTextButton
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon

@Composable
internal fun ReportScreen(router: Router) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    ReportScreenContent(snackbar, router)
}

@Composable
@Preview
private fun ReportScreenPreview() {
    AppTheme {
        ReportScreenContent(SnackbarHostState(), EmptyRouter)
    }
}

@Composable
internal fun ReportScreenContent(snackbar: SnackbarHostState, router: Router) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBarCommon(stringResource(R.string.report_topbar_title), router)
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
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.weight(0.4f))
                    Text(stringResource(R.string.report_description))

                    Spacer(modifier = Modifier.size(24.dp))
                    Text(
                        stringResource(R.string.report_help_section),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    IconTextButton(
                        label = stringResource(R.string.report_help_open_gh_issue),
                        iconId = R.drawable.ic_github,
                        onClick = {
                            router.navigateToUrl(AppUrl.openIssue)
                        },
                    )
                    IconTextButton(
                        label = stringResource(R.string.report_help_email),
                        iconId = R.drawable.ic_email,
                        onClick = {
                            router.navigateToUrl(AppUrl.emailIssue)
                        },
                    )

                    Spacer(modifier = Modifier.size(24.dp))
                    Text(
                        stringResource(R.string.report_feedback_section),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    IconTextButton(
                        label = stringResource(R.string.report_feedback_google_play),
                        iconId = R.drawable.ic_google_play,
                        onClick = {
                            router.navigateToUrl(AppUrl.googlePlay)
                        },
                    )
                    IconTextButton(
                        label = stringResource(R.string.report_feedback_open_gh_issue),
                        iconId = R.drawable.ic_github,
                        onClick = {
                            router.navigateToUrl(AppUrl.openIssue)
                        },
                    )
                    IconTextButton(
                        label = stringResource(R.string.report_feedback_email),
                        iconId = R.drawable.ic_email,
                        onClick = {
                            router.navigateToUrl(AppUrl.emailIssue)
                        },
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
    )
}
