package io.github.nfdz.cryptool.ui.encryption

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.PowerOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.viewModel.EmptyMessageViewModel
import io.github.nfdz.cryptool.shared.message.viewModel.MessageAction
import io.github.nfdz.cryptool.shared.message.viewModel.MessageState
import io.github.nfdz.cryptool.shared.message.viewModel.MessageViewModel
import io.github.nfdz.cryptool.ui.AppMessagesEffect
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.Router
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon
import io.github.nfdz.cryptool.ui.platform.ClipboardAndroid
import io.github.nfdz.cryptool.ui.platform.EmptyClipboardAndroid
import io.github.nfdz.cryptool.ui.supportAdvancedFeatures
import io.github.nfdz.cryptool.ui.topAppBar
import org.koin.core.context.GlobalContext

@Composable
internal fun EncryptionScreen(
    router: Router,
    encryptionId: String,
    encryptionName: String,
    viewModel: MessageViewModel = GlobalContext.get().get(),
    clipboard: ClipboardAndroid = GlobalContext.get().get(),
) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)

    val state = viewModel.observeState().collectAsState()
    val currentState = state.value

    EncryptionScreenLaunchedEffect(viewModel, currentState, encryptionId)

    EncryptionScreenContent(
        snackbar = snackbar,
        viewModel = viewModel,
        clipboard = clipboard,
        router = router,
        state = currentState,
        initialEncryptionName = encryptionName,
        encryptionId = encryptionId,
    )
}

private val previewMessageList = listOf(
    Message(
        id = "1",
        encryptionId = "11",
        message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
        encryptedMessage = "ae42424339fn93555n55",
        timestampInMillis = 2,
        isFavorite = true,
        ownership = MessageOwnership.OTHER,
    ), Message(
        id = "3",
        encryptionId = "11",
        message = "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua",
        encryptedMessage = "42482408r2484f282438849f34f349f",
        timestampInMillis = 3,
        isFavorite = false,
        ownership = MessageOwnership.OWN,
    ), Message(
        id = "4",
        encryptionId = "11",
        message = "'Mark' ➡️ 'Joe'",
        encryptedMessage = "",
        timestampInMillis = 4,
        isFavorite = false,
        ownership = MessageOwnership.SYSTEM,
    ), Message(
        id = "5",
        encryptionId = "11",
        message = "Ut enim ad minim ipsum veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat",
        encryptedMessage = "96596789678967897689",
        timestampInMillis = 5,
        isFavorite = false,
        ownership = MessageOwnership.OWN,
    )
)

private val previewEncryption = Encryption(
    "11",
    "Joe",
    "Pw test",
    AlgorithmVersion.V2,
    MessageSource.Manual,
    false,
    0,
    "",
    0L,
)

@Composable
@Preview
private fun EncryptionScreenPreview() {
    AppTheme {
        EncryptionScreenContent(
            snackbar = SnackbarHostState(),
            encryptionId = "11",
            initialEncryptionName = "Joe",
            viewModel = EmptyMessageViewModel,
            router = EmptyRouter,
            clipboard = EmptyClipboardAndroid,
            state = MessageState(
                messages = previewMessageList,
                selectedMessageIds = setOf(),
                encryption = previewEncryption,
                visibility = true,
                searchText = null,
                searchResultMessageIds = setOf(),
            )
        )
    }
}

@Composable
@Preview
private fun EncryptionScreenSelectModePreview() {
    AppTheme {
        EncryptionScreenContent(
            snackbar = SnackbarHostState(),
            encryptionId = "11",
            initialEncryptionName = "Joe",
            viewModel = EmptyMessageViewModel,
            router = EmptyRouter,
            clipboard = EmptyClipboardAndroid,
            state = MessageState(
                messages = previewMessageList,
                selectedMessageIds = setOf("1", "5"),
                encryption = previewEncryption,
                visibility = true,
                searchText = null,
                searchResultMessageIds = setOf(),
            )
        )
    }
}

@Composable
@Preview
private fun EncryptionScreenSearchModePreview() {
    AppTheme {
        EncryptionScreenContent(
            snackbar = SnackbarHostState(),
            encryptionId = "11",
            initialEncryptionName = "Joe",
            viewModel = EmptyMessageViewModel,
            router = EmptyRouter,
            clipboard = EmptyClipboardAndroid,
            state = MessageState(
                messages = previewMessageList,
                selectedMessageIds = setOf(),
                encryption = previewEncryption,
                visibility = true,
                searchText = "ipsum",
                searchResultMessageIds = setOf("1", "5"),
            )
        )
    }
}

@Composable
private fun EncryptionScreenLaunchedEffect(
    viewModel: MessageViewModel,
    state: MessageState,
    encryptionId: String,
) {
    LaunchedEffect(encryptionId) {
        if (state.encryption?.id != encryptionId) {
            viewModel.dispatch(MessageAction.Initialize(encryptionId))
        }
        viewModel.dispatch(MessageAction.AcknowledgeUnreadMessages(encryptionId))
    }
}

@Composable
internal fun EncryptionScreenContent(
    snackbar: SnackbarHostState,
    viewModel: MessageViewModel,
    clipboard: ClipboardAndroid,
    router: Router,
    state: MessageState,
    encryptionId: String,
    initialEncryptionName: String,
) {
    val encryption = state.encryption
    val encryptionName = encryption?.name ?: initialEncryptionName
    var favoritesOnly by remember { mutableStateOf(false) }
    if (router.supportAdvancedFeatures()) {
        BackHandler(state.selectedMessageIds.isNotEmpty()) {
            viewModel.dispatch(MessageAction.UnselectAll)
        }
    }
    var showEditDialog by remember { mutableStateOf<Encryption?>(null) }
    showEditDialog?.let {
        EditEncryptionDialog(it) {
            showEditDialog = null
        }
    }
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopBar(
                viewModel = viewModel,
                router = router,
                state = state,
                encryptionName = encryptionName,
                favoritesOnly = favoritesOnly,
                onToggleFavoritesOnly = {
                    favoritesOnly = !favoritesOnly
                },
                onShowEditDialog = {
                    showEditDialog = encryption
                },
            )
        },
        content = { padding ->
            if (encryption?.id == encryptionId) {
                val source = encryption.source
                if (source == null) {
                    SourcePicker(
                        modifier = Modifier.padding(padding),
                        router = router,
                    ) {
                        viewModel.dispatch(MessageAction.SetSource(it))
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        MessageList(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                            messages = state.messages,
                            selectedMessageIds = state.selectedMessageIds,
                            searchResultMessageIds = if (state.searchText != null) state.searchResultMessageIds else null,
                            visibility = state.visibility,
                            favoritesOnly = favoritesOnly,
                            onClick = {
                                if (state.selectedMessageIds.isNotEmpty()) {
                                    viewModel.handleSelect(state.selectedMessageIds, it.id)
                                }
                            },
                            onLongClick = {
                                viewModel.handleSelect(state.selectedMessageIds, it.id)
                            },
                            onCopyClick = {
                                clipboard.set(context, snackbar, it)
                            })
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!favoritesOnly && state.searchText == null) {
                            MessageInput(name = encryptionName, source = source, onReceiveMessage = {
                                viewModel.dispatch(MessageAction.ReceiveMessage(it))
                            }, onSendMessage = {
                                viewModel.dispatch(MessageAction.SendMessage(it))
                            })
                        }
                    }
                }
            }
        },
    )
}

private fun MessageViewModel.handleSelect(
    selectedEncryptionId: Set<String>,
    messageId: String,
) {
    if (selectedEncryptionId.contains(messageId)) {
        dispatch(MessageAction.Unselect(messageId))
    } else {
        dispatch(MessageAction.Select(messageId))
    }
}

@Composable
private fun TopBar(
    viewModel: MessageViewModel,
    router: Router,
    state: MessageState,
    encryptionName: String,
    favoritesOnly: Boolean,
    onToggleFavoritesOnly: () -> Unit,
    onShowEditDialog: () -> Unit,
) {
    when {
        state.selectedMessageIds.isNotEmpty() -> {
            SelectModeTopBar(viewModel, router, state)
        }

        state.searchText != null -> {
            SearchModeTopBar(viewModel, state)
        }

        else -> {
            MainTopBar(
                viewModel = viewModel,
                router = router,
                encryptionName = encryptionName,
                state = state,
                favoritesOnly = favoritesOnly,
                onToggleFavoritesOnly = onToggleFavoritesOnly,
                onShowEditDialog = onShowEditDialog,
            )
        }
    }
}

@Composable
private fun MainTopBar(
    viewModel: MessageViewModel,
    router: Router,
    state: MessageState,
    encryptionName: String,
    favoritesOnly: Boolean,
    onToggleFavoritesOnly: () -> Unit,
    onShowEditDialog: () -> Unit,
) {
    TopAppBarCommon(
        router = router,
        title = encryptionName,
        actions = {
            if (state.encryption?.source != null) {
                MainActions(
                    router = router,
                    visibility = state.visibility,
                    onToggleVisibility = { viewModel.dispatch(MessageAction.ToggleVisibility) },
                    hasFavorites = state.messages.any { it.isFavorite },
                    favoritesOnly = favoritesOnly,
                    onToggleFavoritesOnly = onToggleFavoritesOnly,
                    onShowEditDialog = onShowEditDialog,
                    onSearchMessage = { viewModel.dispatch(MessageAction.Search("")) },
                    onChangeMessageSource = { viewModel.dispatch(MessageAction.SetSource(null)) },
                )
            }
        },
    )
}

@Composable
private fun SearchModeTopBar(viewModel: MessageViewModel, state: MessageState) {
    TopAppBar(
        title = {
            SearchInputArea(
                searchText = state.searchText ?: "",
                onSearch = { viewModel.dispatch(MessageAction.Search(it)) }
            )
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.dispatch(MessageAction.Search(null)) }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(R.string.encryption_cancel_search_icon_description)
                )
            }
        },
    )
}

@Composable
private fun SelectModeTopBar(viewModel: MessageViewModel, router: Router, state: MessageState) {
    TopAppBar(
        title = {
            Text(
                state.selectedMessageIds.size.toString(),
                style = MaterialTheme.typography.topAppBar,
            )
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.dispatch(MessageAction.UnselectAll) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.unselect_all))
            }
        },
        actions = { SelectModeActions(viewModel, router, state) },
    )
}

@Composable
private fun MainActions(
    router: Router,
    visibility: Boolean,
    onToggleVisibility: () -> Unit,
    hasFavorites: Boolean,
    favoritesOnly: Boolean,
    onToggleFavoritesOnly: () -> Unit,
    onShowEditDialog: () -> Unit,
    onSearchMessage: () -> Unit,
    onChangeMessageSource: () -> Unit,
) {
    if (router.supportAdvancedFeatures()) {
        IconButton(onClick = onShowEditDialog) {
            Icon(Icons.Rounded.EditNote, stringResource(R.string.encryption_edit_title))
        }
        IconButton(onClick = onChangeMessageSource) {
            Icon(Icons.Rounded.PowerOff, stringResource(R.string.encryption_change_source_icon_description))
        }
    }
    IconButton(onClick = onSearchMessage) {
        Icon(Icons.Rounded.Search, stringResource(R.string.encryption_search_icon_description))
    }
    IconButton(onClick = onToggleVisibility) {
        if (visibility) {
            Icon(
                painter = painterResource(R.drawable.ic_visibility_off),
                stringResource(R.string.encryption_hide_messages_icon_description)
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_visibility),
                stringResource(R.string.encryption_show_messages_icon_description)
            )
        }
    }
    if (hasFavorites || favoritesOnly) {
        IconButton(onClick = onToggleFavoritesOnly) {
            if (favoritesOnly) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorite_off),
                    contentDescription = stringResource(R.string.encryption_show_all_icon_description)
                )
            } else {
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = stringResource(R.string.encryption_show_favorites_only_icon_description)
                )
            }
        }
    }
}

@Composable
private fun SelectModeActions(viewModel: MessageViewModel, router: Router, state: MessageState) {
    var showMenu by remember { mutableStateOf(false) }

    val allFavorites = state.selectedMessageIds.all { id ->
        state.messages.find { it.id == id }?.isFavorite ?: false
    }
    if (allFavorites) {
        IconButton(onClick = {
            viewModel.dispatch(MessageAction.UnsetFavorite(state.selectedMessageIds))
            viewModel.dispatch(MessageAction.UnselectAll)
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_favorite_off),
                contentDescription = stringResource(R.string.encryption_unset_favorites_icon_description)
            )
        }
    } else {
        IconButton(onClick = {
            viewModel.dispatch(MessageAction.SetFavorite(state.selectedMessageIds))
            viewModel.dispatch(MessageAction.UnselectAll)
        }) {
            Icon(
                Icons.Default.Star,
                contentDescription = stringResource(R.string.encryption_set_favorites_icon_description)
            )
        }
    }
    IconButton(onClick = {
        viewModel.dispatch(MessageAction.Remove(state.selectedMessageIds))
        viewModel.dispatch(MessageAction.UnselectAll)
    }) {
        Icon(
            Icons.Rounded.DeleteForever,
            contentDescription = stringResource(R.string.encryption_delete_messages_icon_description)
        )
    }

    if (router.supportAdvancedFeatures()) {
        IconButton(onClick = { showMenu = !showMenu }) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options_icon_description))
        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            val allSelected = state.selectedMessageIds.size == state.messages.size
            if (allSelected) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.unselect_all)) },
                    onClick = {
                        viewModel.dispatch(MessageAction.UnselectAll)
                        showMenu = false
                    },
                )
            } else {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.select_all)) },
                    onClick = {
                        viewModel.dispatch(MessageAction.SelectAll)
                        showMenu = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchInputArea(searchText: String, onSearch: (String?) -> Unit) {
    var input by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusManager.moveFocus(FocusDirection.Up)
    }
    val handleSearch = {
        onSearch(input)
        focusManager.clearFocus()
    }
    TextField(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
            )
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
            capitalization = KeyboardCapitalization.Sentences,
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        placeholder = { Text(stringResource(R.string.encryption_search_hint)) },
        value = input,
        onValueChange = {
            input = it
            onSearch(it)
        },
        keyboardActions = KeyboardActions(
            onSearch = { handleSearch() }
        ),
        trailingIcon = {
            IconButton(onClick = { onSearch(searchText) }) {
                Icon(Icons.Rounded.Search, stringResource(R.string.encryption_search_icon_description))
            }
        },
    )
}

