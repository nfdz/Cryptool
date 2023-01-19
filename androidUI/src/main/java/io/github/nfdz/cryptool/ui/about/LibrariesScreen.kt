package io.github.nfdz.cryptool.ui.about

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Composable
internal fun LibrariesScreen(router: Router) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    var state by remember { mutableStateOf<List<LibraryJson>>(emptyList()) }
    LibrariesSideEffect {
        state = it
    }
    LibrariesScreenContent(snackbar, router, state)
}

@Composable
@Preview
private fun LibrariesScreenPreview() {
    AppTheme {
        LibrariesScreenContent(
            snackbar = SnackbarHostState(),
            router = EmptyRouter,
            state = listOf(
                LibraryJson(
                    name = "Library 1",
                    description = "This is library 1",
                    version = "1.2.3",
                    developers = listOf("Android"),
                    url = "http://android.com",
                    licenses = listOf(LicenseJson("Apache", "url")),
                ),
                LibraryJson(
                    name = "Library 2",
                    description = null,
                    version = "1.2.3",
                    developers = listOf("Android", "Google"),
                    url = null,
                    licenses = listOf(LicenseJson("Apache", "url"), LicenseJson("MIT", "url")),
                ),
            ),
        )
    }
}

@Composable
internal fun LibrariesScreenContent(snackbar: SnackbarHostState, router: Router, state: List<LibraryJson>) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBarCommon(stringResource(R.string.libraries_topbar_title), router)
        },
        content = { padding ->
            if (state.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(state) { library ->
                        Card(
                            Modifier.fillMaxWidth(),
                        ) {
                            Row(Modifier.padding(12.dp)) {
                                Column(Modifier.weight(1f)) {
                                    Text(library.name, style = MaterialTheme.typography.titleLarge)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        library.developers.joinToString(), style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(library.version, style = MaterialTheme.typography.labelMedium)
                                    Spacer(Modifier.height(8.dp))
                                    library.description?.let {
                                        Text(it, style = MaterialTheme.typography.bodyLarge)
                                        Spacer(Modifier.height(8.dp))
                                    }
                                    library.licenses.forEach { license ->
                                        TextButton(onClick = {
                                            router.navigateToUrl(license.url)
                                        }) { Text(license.title, style = MaterialTheme.typography.bodySmall) }
                                    }
                                }
                                library.url?.let { url ->
                                    IconButton(onClick = {
                                        router.navigateToUrl(url)
                                    }) { Icon(Icons.Filled.Link, stringResource(R.string.libraries_open_library_icon_description)) }
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun LibrariesSideEffect(callback: (List<LibraryJson>) -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        val librariesJson = context.readFromAsset()
        callback(parseLibraries(librariesJson))
    }
}

private suspend fun Context.readFromAsset(): String = withContext(Dispatchers.IO) {
    assets.open("open_source_licenses.json").bufferedReader().use {
        it.readText()
    }
}

private suspend fun parseLibraries(librariesJson: String): List<LibraryJson> = withContext(Dispatchers.Default) {
    Json {
        ignoreUnknownKeys = true
    }.decodeFromString(ListSerializer(LibraryJson.serializer()), librariesJson)
}