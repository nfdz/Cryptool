package io.github.nfdz.cryptool.ui.password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.viewModel.*
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import org.koin.core.context.GlobalContext

@Composable
internal fun EditPasswordDialog(
    passwordToEdit: Password,
    viewModel: PasswordViewModel = GlobalContext.get().get(),
    onDismissDialog: () -> Unit
) {
    AutoCloseEffect(onDismissDialog, viewModel)
    Dialog(
        onDismissRequest = onDismissDialog,
        content = { EditPasswordContent(passwordToEdit, viewModel, onDismissDialog) }
    )
}

@Composable
@Preview
private fun EditPasswordScreenPreview() {
    AppTheme {
        EditPasswordContent(
            passwordToEdit = Password(
                "1",
                "Foo",
                "123",
                setOf("Test1", "Test2")
            ),
            viewModel = EmptyPasswordViewModel,
        ) {}
    }
}

@Composable
internal fun EditPasswordContent(passwordToEdit: Password, viewModel: PasswordViewModel, onDismissDialog: () -> Unit) {
    PasswordDialogCommon.DialogContent(
        title = stringResource(R.string.password_edit_title),
        action = stringResource(R.string.dialog_edit),
        initialValues = PasswordDialogCommon.DialogData(
            name = passwordToEdit.name,
            password = passwordToEdit.password,
            tags = Password.joinTags(passwordToEdit.tags),
        ),
        onDismissDialog = onDismissDialog,
        isValid = { isPasswordValid(name = it.name, password = it.password) && it.anyDiff(passwordToEdit) },
        onClick = { viewModel.handleEdit(passwordToEdit, it) },
    )
}

private fun PasswordViewModel.handleEdit(passwordToEdit: Password, data: PasswordDialogCommon.DialogData) {
    if (isPasswordValid(name = data.name, password = data.password)) {
        dispatch(
            PasswordAction.Edit(
                passwordToEdit = passwordToEdit,
                name = data.name,
                password = data.password,
                tags = data.tags
            )
        )
    }
}

@Composable
private fun AutoCloseEffect(onDismissDialog: () -> Unit, viewModel: PasswordViewModel) {
    val effect = viewModel.observeSideEffect().collectAsState(null).value ?: return
    LaunchedEffect(effect) {
        if (effect is PasswordEffect.Edited) {
            onDismissDialog()
        }
    }
}

private fun PasswordDialogCommon.DialogData.anyDiff(other: Password): Boolean {
    return this.name != other.name || this.password != other.password || this.tags != Password.joinTags(other.tags)
}