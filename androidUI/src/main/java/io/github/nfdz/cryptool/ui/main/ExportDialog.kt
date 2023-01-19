package io.github.nfdz.cryptool.ui.main

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.core.export.ExportConfiguration
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.Router

@Composable
fun ExportDialog(
    router: Router,
    snackbar: SnackbarHostState,
    onDismissDialog: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissDialog, content = { ExportDialogContent(router, snackbar, onDismissDialog) })
}

@Composable
@Preview
private fun ExportDialogPreview() {
    AppTheme {
        ExportDialogContent(EmptyRouter, SnackbarHostState()) {}
    }
}


@Composable
private fun ExportDialogContent(
    router: Router,
    snackbar: SnackbarHostState,
    onDismissDialog: () -> Unit,
) {
    ImportExportDialogCommon.DialogContent(
        title = stringResource(R.string.main_export_dialog_title),
        action = stringResource(R.string.main_export_dialog_action),
        onAction = {
            onDismissDialog()
            router.navigateToExportData(
                snackbar,
                it.code.ifEmpty { null },
                ExportConfiguration(
                    encryptions = it.encryptions,
                    messages = it.messages,
                    passwords = it.passwords,
                )
            )
        },
        onDismissDialog = onDismissDialog,
    )
}
