package io.github.nfdz.cryptool.ui.password

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Dangerous
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.Router
import io.github.nfdz.cryptool.ui.platform.ApplicationManager
import io.github.nfdz.cryptool.ui.supportAdvancedFeatures

@Composable
fun PasswordScreenBottomBar(
    router: Router,
    applicationManager: ApplicationManager,
    clipboardHasAppData: Boolean,
    onClearClipboard: () -> Unit,
    onCreateClick: () -> Unit,
) {
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
                    router.navigateToOverlayBall()
                }) {
                    Icon(
                        Icons.Rounded.Login,
                        contentDescription = stringResource(R.string.main_bottombar_open_overlay_icon_description),
                    )
                }
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
            if (router.supportAdvancedFeatures()) {
                FloatingActionButton(onClick = onCreateClick) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.password_bottombar_create_icon_description),
                    )
                }
            }
        },
    )
}
