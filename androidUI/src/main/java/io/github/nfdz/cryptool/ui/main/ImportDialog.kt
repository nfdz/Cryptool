package io.github.nfdz.cryptool.ui.main

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.EmptyRouter
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.Router

@Composable
fun ImportDialog(
    router: Router,
    snackbar: SnackbarHostState,
    onDismissDialog: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissDialog, content = { ImportDialogContent(router, snackbar, onDismissDialog) })
}

@Composable
@Preview
private fun ExportDialogPreview() {
    AppTheme {
        ImportDialogContent(EmptyRouter, SnackbarHostState()) {}
    }
}

@Composable
private fun ImportDialogContent(
    router: Router,
    snackbar: SnackbarHostState,
    onDismissDialog: () -> Unit,
) {
    ImportExportDialogCommon.DialogContent(
        title = stringResource(R.string.main_import_dialog_title),
        action = stringResource(R.string.main_import_dialog_action),
        onAction = {
            onDismissDialog()
            router.navigateToImportData(
                snackbar,
                it.code.ifEmpty { null },
                io.github.nfdz.cryptool.shared.core.import.ImportConfiguration(
                    encryptions = it.encryptions,
                    messages = it.messages,
                    passwords = it.passwords,
                ),
            )
        },
        onDismissDialog = onDismissDialog,
    )
}
