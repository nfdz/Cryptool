package io.github.nfdz.cryptool.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import io.github.nfdz.cryptool.ui.extension.showSnackbarAsync
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperEffect
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.shared.message.viewModel.MessageEffect
import io.github.nfdz.cryptool.shared.message.viewModel.MessageViewModel
import org.koin.core.context.GlobalContext

@Composable
fun AppMessagesEffect(snackbar: SnackbarHostState) {
    MessageSideEffect(snackbar)
    GatekeeperSideEffect(snackbar)
}

@Composable
private fun MessageSideEffect(
    snackbar: SnackbarHostState,
    messageViewModel: MessageViewModel = GlobalContext.get().get()
) {
    val effect = messageViewModel.observeSideEffect().collectAsState(null).value ?: return
    LaunchedEffect(effect) {
        when (effect) {
            is MessageEffect.Error -> snackbar.showSnackbarAsync(effect.message)
            else -> Unit
        }
    }
}

@Composable
private fun GatekeeperSideEffect(
    snackbar: SnackbarHostState,
    gatekeeperViewModel: GatekeeperViewModel = GlobalContext.get().get()
) {
    val effect = gatekeeperViewModel.observeSideEffect().collectAsState(null).value ?: return
    LaunchedEffect(effect) {
        when (effect) {
            is GatekeeperEffect.Error -> snackbar.showSnackbarAsync(effect.message)
            else -> Unit
        }
    }
}