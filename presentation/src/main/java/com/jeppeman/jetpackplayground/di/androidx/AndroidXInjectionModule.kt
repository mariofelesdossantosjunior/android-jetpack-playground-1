package com.jeppeman.jetpackplayground.di.androidx

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds

@Module
abstract class AndroidXInjectionModule {
    @Multibinds
    abstract fun fragmentInjectorFactories(): Map<Class<out Fragment>, AndroidInjector.Factory<out Fragment>>

    @Multibinds
    abstract fun fragmentInjectorFactoriesWithStringKeys(): Map<String, AndroidInjector.Factory<out Fragment>>
}