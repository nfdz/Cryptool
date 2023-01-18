package io.github.nfdz.cryptool.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.platform.version.VersionInformation
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon
import io.github.nfdz.cryptool.ui.platform.ChangelogProviderAndroid

@Composable
internal fun ChangelogScreen(router: Router) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    val releases = ChangelogProviderAndroid(LocalContext.current).all()
    ChangelogScreenContent(snackbar, router, releases)
}

@Composable
@Preview
private fun ChangelogScreenPreview() {
    AppTheme {
        ChangelogScreenContent(
            snackbar = SnackbarHostState(),
            router = EmptyRouter,
            releases = listOf(
                VersionInformation("2.0.0", "Lorem ipsum"),
                VersionInformation("1.2.0", "Lorem ipsum"),
                VersionInformation("1.0.0", "Lorem ipsum"),
            ),
        )
    }
}

@Composable
internal fun ChangelogScreenContent(snackbar: SnackbarHostState, router: Router, releases: List<VersionInformation>) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBarCommon(stringResource(R.string.changelog_topbar_title), router)
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(28.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                items(releases) { release ->
                    Text(release.title, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(release.description, style = MaterialTheme.typography.bodyLarge)
                }
            }
        },
    )
}