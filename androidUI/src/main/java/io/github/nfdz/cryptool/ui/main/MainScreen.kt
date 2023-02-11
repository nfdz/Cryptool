package io.github.nfdz.cryptool.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.viewModel.*
import io.github.nfdz.cryptool.shared.message.viewModel.MessageAction
import io.github.nfdz.cryptool.shared.message.viewModel.MessageViewModel
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon
import io.github.nfdz.cryptool.ui.encryption.CreateEncryptionDialog
import io.github.nfdz.cryptool.ui.platform.ApplicationManager
import io.github.nfdz.cryptool.ui.platform.ClipboardAndroid
import io.github.nfdz.cryptool.ui.platform.EmptyApplicationManager
import io.github.nfdz.cryptool.ui.platform.EmptyClipboardAndroid
import org.koin.core.context.GlobalContext

@Composable
internal fun MainScreen(
    router: Router,
    viewModel: EncryptionViewModel = GlobalContext.get().get(),
    messageViewModel: MessageViewModel = GlobalContext.get().get(),
    clipboard: ClipboardAndroid = GlobalContext.get().get(),
    applicationManager: ApplicationManager = GlobalContext.get().get(),
) {
    MainScreenLaunchedEffect(viewModel, messageViewModel)
    AutoOpenEncryptionEffect(viewModel, router)
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    val state = viewModel.observeState().collectAsState()
    MainScreenContent(
        viewModel = viewModel,
        applicationManager = applicationManager,
        clipboard = clipboard,
        snackbar = snackbar,
        router = router,
        state = state.value,
    )
}

@Composable
@Preview
private fun MainScreenPreview() {
    AppTheme {
        MainScreenContent(
            viewModel = EmptyEncryptionViewModel,
            applicationManager = EmptyApplicationManager,
            clipboard = EmptyClipboardAndroid,
            router = EmptyRouter,
            snackbar = SnackbarHostState(),
            state = EncryptionState(
                encryptions = listOf(
                    Encryption(
                        "1", "Joe", "abc", AlgorithmVersion.V1, MessageSource.Manual, true, 12, "abc", 987688696768
                    ),
                    Encryption(
                        "2", "Mark", "123", AlgorithmVersion.V2, MessageSource.Manual, false, 0, "444", 2345
                    ),
                ),
                selectedEncryptionIds = setOf(),
                initialized = true,
                incomingData = null,
            ),
        )
    }
}

@Composable
@Preview
private fun MainScreenSelectModePreview() {
    AppTheme {
        MainScreenContent(
            viewModel = EmptyEncryptionViewModel,
            clipboard = EmptyClipboardAndroid,
            applicationManager = EmptyApplicationManager,
            router = EmptyRouter,
            snackbar = SnackbarHostState(),
            state = EncryptionState(
                encryptions = listOf(
                    Encryption(
                        "1", "Joe", "abc", AlgorithmVersion.V1, MessageSource.Manual, true, 12, "abc", 987688696768
                    ),
                    Encryption(
                        "2", "Mark", "123", AlgorithmVersion.V2, MessageSource.Manual, false, 0, "444", 2345
                    ),
                ),
                selectedEncryptionIds = setOf("1"),
                initialized = true,
                incomingData = null,
            ),
        )
    }
}

@Composable
private fun MainScreenLaunchedEffect(
    viewModel: EncryptionViewModel,
    messageViewModel: MessageViewModel,
) {
    LaunchedEffect(true) {
        viewModel.dispatch(EncryptionAction.Initialize)
        messageViewModel.dispatch(MessageAction.Close)
    }
}

@Composable
private fun AutoOpenEncryptionEffect(
    viewModel: EncryptionViewModel,
    router: Router,
) {
    val effect = viewModel.observeSideEffect().collectAsState(null).value ?: return
    LaunchedEffect(effect) {
        if (effect is EncryptionEffect.Created) {
            router.navigateToEncryption(
                encryptionId = effect.encryption.id, encryptionName = effect.encryption.name
            )
        }
    }
}

@Composable
internal fun MainScreenContent(
    viewModel: EncryptionViewModel,
    applicationManager: ApplicationManager,
    clipboard: ClipboardAndroid,
    router: Router,
    snackbar: SnackbarHostState,
    state: EncryptionState,
) {
    val context = LocalContext.current
    var clipboardHasAppData by remember { mutableStateOf(clipboard.hasAppData()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    if (showCreateDialog) {
        CreateEncryptionDialog {
            showCreateDialog = false
        }
    }
    var showExportDialog by remember { mutableStateOf(false) }
    if (showExportDialog) {
        ExportDialog(snackbar) {
            showExportDialog = false
        }
    }
    var showImportDialog by remember { mutableStateOf(false) }
    if (showImportDialog) {
        ImportDialog(snackbar) {
            showImportDialog = false
        }
    }

    if (router.supportAdvancedFeatures()) {
        BackHandler(state.selectedEncryptionIds.isNotEmpty()) {
            viewModel.dispatch(EncryptionAction.UnselectAll)
        }

        if (state.initialized && state.incomingData?.isNotEmpty() == true) {
            PickEncryptionDialog(state.incomingData ?: "", state.encryptions) {
                viewModel.dispatch(EncryptionAction.ResolveIncomingData(it?.id))
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        modifier = Modifier,
        topBar = {
            TopBar(
                viewModel,
                router,
                state,
                onExportClick = {
                    showExportDialog = true
                },
                onImportClick = {
                    showImportDialog = true
                },
            )
        },
        bottomBar = {
            MainScreenBottomBar(router, applicationManager, snackbar, clipboardHasAppData, onCreateClick = {
                showCreateDialog = true
            }, onClearClipboard = {
                clipboardHasAppData = false
                clipboard.clear(context, snackbar)
            })
        },
        content = { padding ->
            val noEncryption = state.initialized && state.encryptions.isEmpty()
            if (noEncryption) {
                NoEncryptionContent()
            } else {

                EncryptionList(modifier = Modifier.padding(padding),
                    encryptions = state.encryptions,
                    selectedEncryptionIds = state.selectedEncryptionIds,
                    onClick = {
                        if (state.selectedEncryptionIds.isEmpty()) {
                            router.navigateToEncryption(
                                encryptionId = it.id,
                                encryptionName = it.name,
                            )
                        } else {
                            viewModel.handleSelect(state.selectedEncryptionIds, it.id)
                        }
                    },
                    onLongClick = {
                        viewModel.handleSelect(state.selectedEncryptionIds, it.id)
                    })
            }
        },
    )
}

private fun EncryptionViewModel.handleSelect(
    selectedEncryptionIds: Set<String>,
    encryptionId: String,
) {
    if (selectedEncryptionIds.contains(encryptionId)) {
        dispatch(EncryptionAction.Unselect(encryptionId))
    } else {
        dispatch(EncryptionAction.Select(encryptionId))
    }
}

@Composable
private fun TopBar(
    viewModel: EncryptionViewModel,
    router: Router,
    state: EncryptionState,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    if (state.selectedEncryptionIds.isEmpty()) {
        MainTopBar(router, onExportClick = onExportClick, onImportClick = onImportClick)
    } else {
        SelectModeTopBar(viewModel, router, state)
    }
}

@Composable
private fun MainTopBar(router: Router, onExportClick: () -> Unit, onImportClick: () -> Unit) {
    TopAppBarCommon(
        title = stringResource(R.string.app_name),
        router = null,
        actions = {
            if (router.supportAdvancedFeatures()) {
                MainActions(router = router, onExportClick = onExportClick, onImportClick = onImportClick)
            }
        },
    )
}

@Composable
private fun SelectModeTopBar(viewModel: EncryptionViewModel, router: Router, state: EncryptionState) {
    TopAppBar(
        title = {
            Text(
                state.selectedEncryptionIds.size.toString(),
                style = MaterialTheme.typography.topAppBar,
            )
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.dispatch(EncryptionAction.UnselectAll) }) {
                Icon(Icons.Filled.ArrowBack, stringResource(R.string.unselect_all))
            }
        },
        actions = { SelectModeActions(viewModel, router, state) },
    )
}

@Composable
private fun MainActions(router: Router, onExportClick: () -> Unit, onImportClick: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    IconButton(onClick = {
        showMenu = !showMenu
    }) {
        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options_icon_description))
    }
    DropdownMenu(expanded = showMenu, onDismissRequest = {
        showMenu = false
    }) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.main_import_dialog_title)) },
            onClick = {
                onImportClick()
                showMenu = false
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.main_export_dialog_title)) },
            onClick = {
                onExportClick()
                showMenu = false
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.change_access_topbar_title)) },
            onClick = {
                router.navigateToChangeAccessCode()
                showMenu = false
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.algorithms_topbar_title)) },
            onClick = {
                router.navigateToAlgorithms()
                showMenu = false
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.report_topbar_title)) },
            onClick = {
                router.navigateToReport()
                showMenu = false
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.about_topbar_title)) },
            onClick = {
                router.navigateToAbout()
                showMenu = false
            },
        )
    }
}

@Composable
private fun SelectModeActions(viewModel: EncryptionViewModel, router: Router, state: EncryptionState) {
    var showMenu by remember { mutableStateOf(false) }

    val allFavorites = state.selectedEncryptionIds.all { id ->
        state.encryptions.find { it.id == id }?.isFavorite ?: false
    }
    if (allFavorites) {
        IconButton(onClick = {
            viewModel.dispatch(EncryptionAction.UnsetFavorite(state.selectedEncryptionIds))
            viewModel.dispatch(EncryptionAction.UnselectAll)
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_favorite_off),
                contentDescription = stringResource(R.string.main_unset_favorites_icon_description)
            )
        }
    } else {
        IconButton(onClick = {
            viewModel.dispatch(EncryptionAction.SetFavorite(state.selectedEncryptionIds))
            viewModel.dispatch(EncryptionAction.UnselectAll)
        }) {
            Icon(
                Icons.Default.Star, contentDescription = stringResource(R.string.main_set_favorites_icon_description)
            )
        }
    }
    IconButton(onClick = {
        viewModel.dispatch(EncryptionAction.Remove(state.selectedEncryptionIds))
        viewModel.dispatch(EncryptionAction.UnselectAll)
    }) {
        Icon(Icons.Rounded.DeleteForever, contentDescription = stringResource(R.string.main_delete_icon_description))
    }

    if (router.supportAdvancedFeatures()) {
        IconButton(onClick = { showMenu = !showMenu }) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options_icon_description))
        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            val allSelected = state.selectedEncryptionIds.size == state.encryptions.size
            if (allSelected) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.unselect_all)) },
                    onClick = {
                        viewModel.dispatch(EncryptionAction.UnselectAll)
                        showMenu = false
                    },
                )
            } else {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.select_all)) },
                    onClick = {
                        viewModel.dispatch(EncryptionAction.SelectAll)
                        showMenu = false
                    },
                )
            }
        }
    }
}