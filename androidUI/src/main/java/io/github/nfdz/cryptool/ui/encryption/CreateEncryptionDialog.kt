package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.shared.encryption.entity.isEncryptionValid
import io.github.nfdz.cryptool.shared.encryption.viewModel.EmptyEncryptionViewModel
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionAction
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionEffect
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionViewModel
import io.github.nfdz.cryptool.ui.AppTheme
import org.koin.core.context.GlobalContext

@Composable
internal fun CreateEncryptionDialog(
    viewModel: EncryptionViewModel = GlobalContext.get().get(),
    onDismissDialog: () -> Unit
) {
    AutoCloseEffect(onDismissDialog, viewModel)
    Dialog(
        onDismissRequest = onDismissDialog,
        content = { CreateEncryptionContent(viewModel, onDismissDialog) }
    )
}

@Composable
@Preview
private fun CreateEncryptionPreview() {
    AppTheme {
        CreateEncryptionContent(viewModel = EmptyEncryptionViewModel) {}
    }
}


@Composable
internal fun CreateEncryptionContent(viewModel: EncryptionViewModel, onDismissDialog: () -> Unit) {
    EncryptionDialogCommon.DialogContent(
        title = stringResource(R.string.encryption_new_title),
        action = stringResource(R.string.dialog_create),
        initialValues = null,
        onDismissDialog = onDismissDialog,
        isValid = { isEncryptionValid(name = it.name, password = it.password) },
        onClick = { viewModel.handleCreate(it) },
    )
}

private fun EncryptionViewModel.handleCreate(data: EncryptionDialogCommon.DialogData) {
    if (isEncryptionValid(name = data.name, password = data.password)) {
        dispatch(EncryptionAction.Create(name = data.name, password = data.password, algorithm = data.algorithm))
    }
}

@Composable
private fun AutoCloseEffect(
    onDismissDialog: () -> Unit,
    viewModel: EncryptionViewModel,
) {
    val effect = viewModel.observeSideEffect().collectAsState(null).value ?: return
    LaunchedEffect(effect) {
        if (effect is EncryptionEffect.Created) {
            onDismissDialog()
        }
    }
}
