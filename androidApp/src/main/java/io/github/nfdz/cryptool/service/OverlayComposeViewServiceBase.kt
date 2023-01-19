package io.github.nfdz.cryptool.service

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

abstract class OverlayComposeViewServiceBase : OverlayViewServiceBase() {

    private val viewModelStoreOwner = ServiceViewModelStoreOwner()
    private val lifecycleOwner = ServiceLifecycleOwner()
    private val savedStateRegistryController = ServiceSavedStateRegistryController(lifecycleOwner)
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
        ViewTreeViewModelStoreOwner.set(view, viewModelStoreOwner)
        ViewTreeLifecycleOwner.set(view, lifecycleOwner)
        view.setViewTreeSavedStateRegistryOwner(savedStateRegistryController)
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
    private val store = ViewModelStore()

    override fun getViewModelStore(): ViewModelStore = store
}

class ServiceLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry { lifecycle }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

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
}

class ServiceSavedStateRegistryController(private val lifecycleOwner: LifecycleOwner) : SavedStateRegistryOwner {
    private val _savedStateRegistryController = SavedStateRegistryController.create(this)

    override val savedStateRegistry = _savedStateRegistryController.savedStateRegistry
    override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle

    fun performRestore(savedState: Bundle? = null) = _savedStateRegistryController.performRestore(savedState)
}