package io.github.nfdz.cryptool.ui.main

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.platform.file.EmptyImportFile
import io.github.nfdz.cryptool.shared.platform.file.ImportFile
import io.github.nfdz.cryptool.shared.platform.file.ImportFileState
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.showSnackbarAsync
import org.koin.core.context.GlobalContext

@Composable
fun ImportDialog(
    snackbar: SnackbarHostState,
    importFile: ImportFile = GlobalContext.get().get(),
    onDismissDialog: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissDialog,
        content = { ImportDialogContent(importFile, snackbar, onDismissDialog) })
}

@Composable
@Preview
private fun ExportDialogPreview() {
    AppTheme {
        ImportDialogContent(EmptyImportFile, SnackbarHostState()) {}
    }
}

@Composable
private fun ImportDialogContent(
    importFile: ImportFile,
    snackbar: SnackbarHostState,
    onDismissDialog: () -> Unit,
) {
    val context = LocalContext.current
    var state by remember { mutableStateOf<ImportExportDialogCommon.DialogData?>(null) }
    val launcherPickFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        onDismissDialog()
        val data = state
        if (data != null && uri != null) {
            importFile.import(
                uri,
                data.code.ifEmpty { null },
                data.toConfiguration(),
            ) {
                snackbar.showState(context, it)
            }
        }
    }
    ImportExportDialogCommon.DialogContent(
        title = stringResource(R.string.main_import_dialog_title),
        action = stringResource(R.string.main_import_dialog_action),
        onAction = {
            state = it
            launcherPickFile.launch(arrayOf(mimeType))
        },
        onDismissDialog = onDismissDialog,
    )
}

private const val mimeType = "*/*"

private fun SnackbarHostState.showState(context: Context, state: ImportFileState) {
    val message = when (state) {
        ImportFileState.Error -> context.getString(R.string.import_error_snackbar)
        ImportFileState.InProgress -> context.getString(R.string.import_in_progress_snackbar)
        ImportFileState.SuccessEmpty -> context.getString(R.string.import_success_empty_snackbar)
        is ImportFileState.Success -> context.getString(
            R.string.import_success_snackbar,
            state.result.encryptions,
            state.result.messages,
            state.result.passwords,
        )
    }
    showSnackbarAsync(message)
}
