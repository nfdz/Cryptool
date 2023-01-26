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
import io.github.nfdz.cryptool.shared.platform.file.EmptyExportFile
import io.github.nfdz.cryptool.shared.platform.file.ExportFile
import io.github.nfdz.cryptool.shared.platform.file.ExportFileState
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.showSnackbarAsync
import org.koin.core.context.GlobalContext
import java.text.DateFormat.getDateInstance
import java.util.*

@Composable
fun ExportDialog(
    snackbar: SnackbarHostState,
    exportFile: ExportFile = GlobalContext.get().get(),
    onDismissDialog: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissDialog,
        content = { ExportDialogContent(exportFile, snackbar, onDismissDialog) },
    )
}

@Composable
@Preview
private fun ExportDialogPreview() {
    AppTheme {
        ExportDialogContent(EmptyExportFile, SnackbarHostState()) {}
    }
}


@Composable
private fun ExportDialogContent(
    exportFile: ExportFile,
    snackbar: SnackbarHostState,
    onDismissDialog: () -> Unit,
) {
    val context = LocalContext.current
    var state by remember { mutableStateOf<ImportExportDialogCommon.DialogData?>(null) }
    val launcherPickFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(mimeType)) { uri ->
        onDismissDialog()
        val data = state
        if (data != null && uri != null) {
            exportFile.export(
                uri,
                data.code.ifEmpty { null },
                data.toConfiguration(),
            ) {
                snackbar.showState(context, it)
            }
        }
    }
    ImportExportDialogCommon.DialogContent(
        title = stringResource(R.string.main_export_dialog_title),
        action = stringResource(R.string.main_export_dialog_action),
        onAction = {
            state = it
            launcherPickFile.launch(generateSuggestedName())
        },
        onDismissDialog = onDismissDialog,
    )
}

private fun generateSuggestedName(): String {
    val time = getDateInstance().format(Date())
    return "$time.cryptool"
}

private const val mimeType = "*/*"

private fun SnackbarHostState.showState(context: Context, state: ExportFileState) {
    val message = when (state) {
        ExportFileState.IN_PROGRESS -> context.getString(R.string.export_in_progress_snackbar)
        ExportFileState.SUCCESS -> context.getString(R.string.export_success_snackbar)
        ExportFileState.ERROR -> context.getString(R.string.export_error_snackbar)
    }
    showSnackbarAsync(message)
}
