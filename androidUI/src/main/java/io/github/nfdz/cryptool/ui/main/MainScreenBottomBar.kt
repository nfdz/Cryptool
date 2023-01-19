package io.github.nfdz.cryptool.ui.main

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Dangerous
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.Router
import io.github.nfdz.cryptool.ui.platform.ApplicationManager
import kotlinx.coroutines.launch

@Composable
fun MainScreenBottomBar(
    router: Router,
    applicationManager: ApplicationManager,
    snackbar: SnackbarHostState,
    clipboardHasAppData: Boolean,
    onClearClipboard: () -> Unit,
    onCreateClick: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    BottomAppBar(
        actions = {
            IconButton(onClick = {
                applicationManager.stopApp()
            }) {
                Icon(
                    Icons.Rounded.Dangerous,
                    contentDescription = stringResource(R.string.main_bottombar_finish_app_icon_description),
                )
            }
            if (!router.isOverlayMode) {
                IconButton(onClick = {
                    scope.launch {
                        if (router.navigateToOverlayPermission()) {
                            router.navigateToOverlayBall()
                        } else {
                            val result = snackbar.showSnackbar(
                                context.getString(R.string.main_open_overlay_permission_error),
                                actionLabel = context.getString(R.string.main_open_overlay_permission_action),
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                router.navigateToOverlayPermissionSettings()
                            }
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_open_overlay),
                        contentDescription = stringResource(R.string.main_bottombar_open_overlay_icon_description),
                    )
                }
            }
            IconButton(onClick = { router.navigateToPasswords() }) {
                Icon(
                    modifier = Modifier.size(38.dp),
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.main_bottombar_open_passwords_icon_description),
                )
            }
            if (clipboardHasAppData) {
                IconButton(onClick = onClearClipboard) {
                    Icon(
                        Icons.Filled.PendingActions,
                        contentDescription = stringResource(R.string.main_bottombar_clear_clipboard_icon_description),
                    )
                }
            }
        },
        floatingActionButton = {
            if (!router.isOverlayMode) {
                FloatingActionButton(onClick = onCreateClick) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.main_bottombar_create_icon_description)
                    )
                }
            } else {
                FloatingActionButton(onClick = {
                    router.exitOverlay()
                }) {
                    Icon(
                        Icons.Rounded.Fullscreen,
                        contentDescription = stringResource(R.string.main_bottombar_open_full_icon_description)
                    )
                }
            }
        }
    )
}