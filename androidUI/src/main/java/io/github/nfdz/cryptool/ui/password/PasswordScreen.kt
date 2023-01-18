package io.github.nfdz.cryptool.ui.password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.viewModel.*
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon
import io.github.nfdz.cryptool.ui.platform.ApplicationManager
import io.github.nfdz.cryptool.ui.platform.ClipboardAndroid
import io.github.nfdz.cryptool.ui.platform.EmptyApplicationManager
import io.github.nfdz.cryptool.ui.platform.EmptyClipboardAndroid
import org.koin.core.context.GlobalContext

@Composable
internal fun PasswordScreen(
    router: Router,
    viewModel: PasswordViewModel = GlobalContext.get().get(),
    clipboard: ClipboardAndroid = GlobalContext.get().get(),
    applicationManager: ApplicationManager = GlobalContext.get().get(),
) {
    PasswordScreenSideEffect(viewModel)

    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)

    val state = viewModel.observeState().collectAsState()

    PasswordScreenContent(
        snackbar = snackbar,
        clipboard = clipboard,
        applicationManager = applicationManager,
        viewModel = viewModel,
        router = router,
        state = state.value,
    )
}

@Composable
@Preview
private fun PasswordScreenPreview() {
    AppTheme {
        PasswordScreenContent(
            snackbar = SnackbarHostState(),
            clipboard = EmptyClipboardAndroid,
            applicationManager = EmptyApplicationManager,
            viewModel = EmptyPasswordViewModel,
            router = EmptyRouter,
            state = PasswordState(
                initialized = true,
                passwords = (1..10).map {
                    Password(
                        id = "$it",
                        name = "Test $it",
                        password = "Password $it",
                        tags = setOf("Music")
                    )
                },
                selectedTags = setOf(),
                tags = listOf("Music", "Cinema", "Cars", "Science"),
            ),
        )
    }
}

@Composable
private fun PasswordScreenSideEffect(viewModel: PasswordViewModel) {
    LaunchedEffect(true) {
        viewModel.dispatch(PasswordAction.Initialize)
    }
}

@Composable
internal fun PasswordScreenContent(
    snackbar: SnackbarHostState,
    applicationManager: ApplicationManager,
    clipboard: ClipboardAndroid,
    viewModel: PasswordViewModel,
    router: Router,
    state: PasswordState,
) {
    var clipboardHasAppData by remember { mutableStateOf(clipboard.hasAppData()) }
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    if (showCreateDialog) {
        CreatePasswordDialog {
            showCreateDialog = false
        }
    }
    var showEditDialog by remember { mutableStateOf<Password?>(null) }
    showEditDialog?.let {
        EditPasswordDialog(it) {
            showEditDialog = null
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            PasswordScreenBottomBar(
                router,
                applicationManager,
                clipboardHasAppData,
                onClearClipboard = {
                    clipboardHasAppData = false
                    clipboard.clear(context, snackbar)
                },
                onCreateClick = {
                    showCreateDialog = true
                }
            )
        },
        topBar = {
            TopAppBarCommon(stringResource(R.string.password_topbar_title), router)
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val noPassword = state.initialized && state.passwords.isEmpty()
                if (noPassword) {
                    NoPasswordContent()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 90.dp),
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            TagsList(state.tags, state.selectedTags) { tag ->
                                viewModel.dispatch(
                                    if (state.selectedTags.contains(tag)) {
                                        PasswordAction.RemoveFilter(tag)
                                    } else {
                                        PasswordAction.AddFilter(tag)
                                    }
                                )
                            }
                        }

                        itemsIndexed(state.filteredPassword) { _, password ->
                            PasswordItem(
                                password,
                                supportAdvancedFeatures = router.supportAdvancedFeatures(),
                                onClick = {
                                    if (router.supportAdvancedFeatures()) {
                                        showEditDialog = password
                                    }
                                },
                                onCopyPassword = {
                                    clipboardHasAppData = true
                                    clipboard.set(context, snackbar, password.password)
                                },
                                onDeletePassword = {
                                    viewModel.dispatch(PasswordAction.Remove(password))
                                },
                            )
                        }
                    }
                }
            }
        },
    )
}

