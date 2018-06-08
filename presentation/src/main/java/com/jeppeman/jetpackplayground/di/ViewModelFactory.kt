package com.jeppeman.jetpackplayground.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeppeman.jetpackplayground.di.scopes.PerFragment
import javax.inject.Inject
import javax.inject.Provider

@PerFragment
class ViewModelFactory @Inject constructor(
        private val viewModelProviders: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModelProviders[modelClass]?.get() as T
    }
}