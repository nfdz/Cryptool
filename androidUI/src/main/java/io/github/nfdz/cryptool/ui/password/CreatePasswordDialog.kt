package io.github.nfdz.cryptool.ui.password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.password.viewModel.*
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import org.koin.core.context.GlobalContext

@Composable
internal fun CreatePasswordDialog(
    viewModel: PasswordViewModel = GlobalContext.get().get(),
    onDismissDialog: () -> Unit,
) {
    AutoCloseEffect(onDismissDialog, viewModel)
    Dialog(onDismissRequest = onDismissDialog, content = { CreatePasswordContent(viewModel, onDismissDialog) })
}

@Composable
@Preview
private fun CreatePasswordScreenPreview() {
    AppTheme {
        CreatePasswordContent(viewModel = EmptyPasswordViewModel) {}
    }
}

@Composable
internal fun CreatePasswordContent(viewModel: PasswordViewModel, onDismissDialog: () -> Unit) {
    PasswordDialogCommon.DialogContent(
        title = stringResource(R.string.password_new_title),
        action = stringResource(R.string.dialog_create),
        onDismissDialog = onDismissDialog,
        initialValues = null,
        isValid = { isPasswordValid(name = it.name, password = it.password) },
        onClick = { viewModel.handleCreate(it) },
    )
}

private fun PasswordViewModel.handleCreate(data: PasswordDialogCommon.DialogData) {
    if (isPasswordValid(name = data.name, password = data.password)) {
        dispatch(
            PasswordAction.Create(
                name = data.name,
                password = data.password,
                tags = data.tags,
            )
        )
    }
}

@Composable
private fun AutoCloseEffect(onDismissDialog: () -> Unit, viewModel: PasswordViewModel) {
    val effect = viewModel.observeSideEffect().collectAsState(null).value ?: return
    LaunchedEffect(effect) {
        if (effect is PasswordEffect.Created) {
            onDismissDialog()
        }
    }
}
