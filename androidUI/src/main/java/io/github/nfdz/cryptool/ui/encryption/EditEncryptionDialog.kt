package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.isEncryptionValid
import io.github.nfdz.cryptool.shared.encryption.viewModel.EmptyEncryptionViewModel
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionAction
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionEffect
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionViewModel
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import org.koin.core.context.GlobalContext

@Composable
internal fun EditEncryptionDialog(
    encryptionToEdit: Encryption,
    viewModel: EncryptionViewModel = GlobalContext.get().get(),
    onDismissDialog: () -> Unit
) {
    AutoCloseEffect(onDismissDialog, viewModel)
    Dialog(
        onDismissRequest = onDismissDialog,
        content = { EditEncryptionContent(encryptionToEdit, viewModel, onDismissDialog) }
    )
}

@Composable
@Preview
private fun EditEncryptionPreview() {
    AppTheme {
        EditEncryptionContent(
            onDismissDialog = { },
            encryptionToEdit = Encryption(
                "",
                "Name",
                "password",
                AlgorithmVersion.V2,
                null,
                false,
                0,
                "",
                0L,
            ),
            viewModel = EmptyEncryptionViewModel,
        )
    }
}


@Composable
internal fun EditEncryptionContent(
    encryptionToEdit: Encryption,
    viewModel: EncryptionViewModel,
    onDismissDialog: () -> Unit
) {
    EncryptionDialogCommon.DialogContent(
        title = stringResource(R.string.encryption_edit_title),
        action = stringResource(R.string.dialog_edit),
        initialValues = EncryptionDialogCommon.DialogData(
            name = encryptionToEdit.name,
            password = encryptionToEdit.password,
            algorithm = encryptionToEdit.algorithm,
        ),
        onDismissDialog = onDismissDialog,
        isValid = { isEncryptionValid(name = it.name, password = it.password) && it.anyDiff(encryptionToEdit) },
        onClick = { viewModel.handleEdit(encryptionToEdit, it) },
    )
}

private fun EncryptionViewModel.handleEdit(encryptionToEdit: Encryption, data: EncryptionDialogCommon.DialogData) {
    if (isEncryptionValid(name = data.name, password = data.password)) {
        dispatch(
            EncryptionAction.Edit(
                encryptionToEdit = encryptionToEdit,
                name = data.name,
                password = data.password,
                algorithm = data.algorithm
            )
        )
    }
}

private fun EncryptionDialogCommon.DialogData.anyDiff(other: Encryption): Boolean {
    return this.name != other.name || this.password != other.password || this.algorithm != other.algorithm
}

@Composable
private fun AutoCloseEffect(
    onDismissDialog: () -> Unit,
    viewModel: EncryptionViewModel,
) {
    val effect = viewModel.observeSideEffect().collectAsState(null).value ?: return
    LaunchedEffect(effect) {
        if (effect is EncryptionEffect.Edited) {
            onDismissDialog()
        }
    }
}
