package io.github.nfdz.cryptool.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.core.constant.AppUrl
import io.github.nfdz.cryptool.shared.platform.version.EmptyVersionProvider
import io.github.nfdz.cryptool.shared.platform.version.VersionProvider
import io.github.nfdz.cryptool.ui.AppMessagesEffect
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.BuildConfig
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.Router
import io.github.nfdz.cryptool.ui.common.IconTextButton
import io.github.nfdz.cryptool.ui.common.LargeAppIcon
import io.github.nfdz.cryptool.ui.common.LargeAppName
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon
import org.koin.core.context.GlobalContext


@Composable
internal fun AboutScreen(
    router: Router,
    versionProvider: VersionProvider = GlobalContext.get().get(),
) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    AboutScreenContent(snackbar, router, versionName = BuildConfig.VERSION_NAME, versionProvider)
}

@Composable
@Preview
private fun AboutScreenPreview() {
    AppTheme {
        AboutScreenContent(SnackbarHostState(), EmptyRouter, versionName = "3.0.0", EmptyVersionProvider)
    }
}

@Composable
internal fun AboutScreenContent(
    snackbar: SnackbarHostState,
    router: Router,
    versionName: String,
    versionProvider: VersionProvider,
) {
    val unknownCertificate = stringResource(R.string.about_certificate_unknown)
    var certificate by remember { mutableStateOf(unknownCertificate) }

    LaunchedEffect(Unit) {
        versionProvider.getAppCertificate()?.let { certificate = it }
    }

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
                            versionName,
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
                    Spacer(modifier = Modifier.size(18.dp))
                    Text(
                        certificate,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
    )
}