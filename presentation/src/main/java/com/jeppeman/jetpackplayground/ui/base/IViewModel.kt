package com.jeppeman.jetpackplayground.ui.base

import androidx.databinding.Observable
import androidx.lifecycle.LifecycleObserver
import com.jeppeman.jetpackplayground.navigation.NavigationRequest
import com.jeppeman.jetpackplayground.navigation.NavigationRequestListener

interface IViewModel : LifecycleObserver, Observable {
    fun registerNavigationRequestListener(listener: NavigationRequestListener)
    fun unregisterNavigationRequestListener(listener: NavigationRequestListener)
    fun requestNavigation(navigationRequest: NavigationRequest)
}