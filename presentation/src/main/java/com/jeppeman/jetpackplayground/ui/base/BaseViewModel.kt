package com.jeppeman.jetpackplayground.ui.base

import androidx.annotation.CallSuper
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.jeppeman.jetpackplayground.domain.executor.PostExecutionThread
import com.jeppeman.jetpackplayground.navigation.NavigationRequest
import com.jeppeman.jetpackplayground.navigation.NavigationRequestListener
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

abstract class BaseViewModel : ViewModel(), IViewModel {
    @Inject
    lateinit var postExecutionThread: PostExecutionThread
    private val disposables = CompositeDisposable()
    private var isInitialized = false
    private val navigationRequestListeners = mutableListOf<NavigationRequestListener>()
    private lateinit var propertyChangedCallbacks: PropertyChangeRegistry

    protected fun Disposable.disposeOnCleared() {
        disposables.add(this)
    }

    protected fun <T> Single<T>.observeOnPostExecution(): Single<T> {
        return observeOn(postExecutionThread.getScheduler())
    }

    protected open fun onInitialize() {
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected open fun onCreate() {
        if (!isInitialized) {
            isInitialized = true
            onInitialize()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected open fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected open fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected open fun onDestroy() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected open fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected open fun onPause() {
    }

    override fun requestNavigation(navigationRequest: NavigationRequest) {
        navigationRequestListeners.forEach { listener -> listener.onNavigationRequest(navigationRequest) }
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    @CallSuper
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        synchronized(this) {
            if (!::propertyChangedCallbacks.isInitialized) {
                propertyChangedCallbacks = PropertyChangeRegistry()
            }
        }
        propertyChangedCallbacks.add(callback)
    }

    @CallSuper
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        if (::propertyChangedCallbacks.isInitialized) {
            propertyChangedCallbacks.remove(callback)
        }
    }

    override fun registerNavigationRequestListener(listener: NavigationRequestListener) {
        navigationRequestListeners.add(listener)
    }

    override fun unregisterNavigationRequestListener(listener: NavigationRequestListener) {
        navigationRequestListeners.remove(listener)
    }

    fun notifyChange() {
        if (::propertyChangedCallbacks.isInitialized) {
            propertyChangedCallbacks.notifyCallbacks(this, 0, null)
        }
    }

    fun notifyPropertyChanged(fieldId: Int) {
        if (::propertyChangedCallbacks.isInitialized) {
            propertyChangedCallbacks.notifyCallbacks(this, fieldId, null)
        }
    }
}
