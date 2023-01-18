package io.github.nfdz.cryptool.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.core.constant.AppUrl
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.IconTextButton
import io.github.nfdz.cryptool.ui.common.LargeAppIcon
import io.github.nfdz.cryptool.ui.common.LargeAppName
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon

@Composable
internal fun AboutScreen(router: Router) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    AboutScreenContent(snackbar, router)
}

@Composable
@Preview
private fun AboutScreenPreview() {
    AppTheme {
        AboutScreenContent(SnackbarHostState(), EmptyRouter)
    }
}

@Composable
internal fun AboutScreenContent(snackbar: SnackbarHostState, router: Router) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBarCommon(stringResource(R.string.about_topbar_title), router)
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
                    LargeAppIcon()
                    LargeAppName()
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        stringResource(R.string.app_slogan),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(28.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .clickable { router.navigateToChangelog() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            BuildConfig.VERSION_NAME,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 24.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    IconTextButton(
                        label = stringResource(R.string.about_official_website),
                        iconId = R.drawable.ic_web_icon,
                        onClick = {
                            router.navigateToUrl(AppUrl.officialWebsite)
                        },
                    )
                    IconTextButton(
                        label = stringResource(R.string.about_source_code),
                        iconId = R.drawable.ic_github,
                        onClick = {
                            router.navigateToUrl(AppUrl.sourceCode)
                        },
                    )
                    IconTextButton(
                        label = stringResource(R.string.about_libraries),
                        iconId = R.drawable.ic_libraries,
                        onClick = {
                            router.navigateToLibraries()
                        },
                    )
                    IconTextButton(
                        label = stringResource(R.string.about_license),
                        iconId = R.drawable.ic_copyleft,
                        onClick = {
                            router.navigateToUrl(AppUrl.license)
                        },
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
    )
}