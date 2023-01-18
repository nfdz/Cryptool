package io.github.nfdz.cryptool.shared.core.viewModel

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface State
interface Action
interface Effect

interface NanoViewModel<S : State, A : Action, E : Effect> {
    fun observeState(): StateFlow<S>
    fun observeSideEffect(): Flow<E>
    fun dispatch(action: A)
}

abstract class NanoViewModelBase<S : State, A : Action, E : Effect> : NanoViewModel<S, A, E>,
    CoroutineScope by CoroutineScope(Dispatchers.Default.limitedParallelism(1)) {

    companion object {
        private const val actionsChannelCapacity = 100
    }

    private val state by lazy { MutableStateFlow(initialState) }
    private val sideEffect by lazy { MutableSharedFlow<E>() }
    private val actionsChannel by lazy {
        Channel<A>(capacity = actionsChannelCapacity).apply {
            launch {
                consumeEach {
                    Napier.d(tag = tag, message = "processAction: $it")
                    processAction(it)
                }
            }
        }
    }

    override fun observeState(): StateFlow<S> = state
    override fun observeSideEffect(): Flow<E> = sideEffect
    override fun dispatch(action: A) {
        launch {
            actionsChannel.send(action)
        }
    }

    protected abstract val tag: String
    protected abstract val initialState: S
    protected abstract suspend fun processAction(action: A)

    protected fun emitNewState(newState: S) {
        Napier.d(tag = tag, message = "emitNewState: $newState")
        state.value = newState
    }

    protected suspend fun emitSideEffect(newEffect: E) {
        Napier.d(tag = tag, message = "emitSideEffect: $newEffect")
        sideEffect.emit(newEffect)
    }

    protected val currentState: S
        get() = state.value

}