package io.github.nfdz.cryptool.service

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

abstract class OverlayComposeViewServiceBase : OverlayViewServiceBase() {

    private val viewModelStoreOwner = ServiceViewModelStoreOwner()
    private val lifecycleOwner = ServiceLifecycleOwner()
    private val savedStateRegistryController = ServiceSavedStateRegistryController(lifecycleOwner.lifecycle)
    private val composeView: View by lazy {
        ComposeView(this).apply {
            setContent {
                OverlayContent()
            }
        }
    }

    override val view: FrameLayout by lazy { FrameLayout(this) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        view.setViewTreeLifecycleOwner(lifecycleOwner)
        view.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        view.setViewTreeSavedStateRegistryOwner(savedStateRegistryController)
        view.setViewTreeOnBackPressedDispatcherOwner(ServiceOnBackPressedDispatcherOwner(lifecycleOwner.lifecycle))
        lifecycleOwner.onCreate()
        view.addView(composeView)
    }

    override fun onDestroy() {
        lifecycleOwner.onDestroy()
        super.onDestroy()
    }

    @Composable
    abstract fun OverlayContent()
}

class ServiceViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()
}

class ServiceLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    fun onCreate() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}

class ServiceSavedStateRegistryController(override val lifecycle: Lifecycle) : SavedStateRegistryOwner {
    private val _savedStateRegistryController = SavedStateRegistryController.create(this)

    override val savedStateRegistry = _savedStateRegistryController.savedStateRegistry

    fun performRestore(savedState: Bundle? = null) = _savedStateRegistryController.performRestore(savedState)
}

class ServiceOnBackPressedDispatcherOwner(override val lifecycle: Lifecycle) : OnBackPressedDispatcherOwner {
    override val onBackPressedDispatcher: OnBackPressedDispatcher = OnBackPressedDispatcher(null)
}