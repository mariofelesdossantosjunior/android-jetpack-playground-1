package com.jeppeman.jetpackplayground.ui

import androidx.lifecycle.ViewModelProviders
import com.jeppeman.jetpackplayground.di.scopes.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ContainerModule {
    @Provides
    @PerActivity
    fun provideContainerViewModel(containerActivity: ContainerActivity): ContainerViewModel {
        return ViewModelProviders.of(containerActivity)[ContainerViewModel::class.java]
    }
}