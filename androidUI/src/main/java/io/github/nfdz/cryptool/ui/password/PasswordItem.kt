package io.github.nfdz.cryptool.ui.password

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.hidePassword

@Composable
fun PasswordItem(
    key: Password,
    modifier: Modifier = Modifier,
    supportAdvancedFeatures: Boolean,
    onClick: () -> Unit,
    onCopyPassword: () -> Unit,
    onDeletePassword: () -> Unit
) {
    var passwordVisibility by remember { mutableStateOf(false) }
    var deleteDialogVisibility by remember { mutableStateOf(false) }

    Row(
        modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                text = key.name,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium,
                text = if (passwordVisibility) key.password else key.password.hidePassword(),
            )
        }
        IconButton(onClick = { onCopyPassword() }) {
            Icon(Icons.Rounded.FileCopy, contentDescription = stringResource(R.string.password_copy_icon_description))
        }
        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
            if (passwordVisibility) {
                Icon(
                    painter = painterResource(R.drawable.ic_visibility_off),
                    contentDescription = stringResource(R.string.password_hide_icon_description)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_visibility),
                    contentDescription = stringResource(R.string.password_show_icon_description)
                )
            }
        }
        if (supportAdvancedFeatures) {
            IconButton(onClick = { deleteDialogVisibility = true }) {
                Icon(
                    Icons.Rounded.DeleteForever,
                    contentDescription = stringResource(R.string.password_delete_icon_description)
                )
            }
        }
    }

    if (deleteDialogVisibility) {
        ConfirmDeleteDialog(onDismiss = {
            deleteDialogVisibility = false
        }, onConfirm = {
            deleteDialogVisibility = false
            onDeletePassword()
        })
    }
}

@Composable
private fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.password_delete_dialog_action)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
        title = { Text(stringResource(R.string.password_delete_dialog_title)) },
        text = { Text(stringResource(R.string.password_delete_dialog_description)) })
}