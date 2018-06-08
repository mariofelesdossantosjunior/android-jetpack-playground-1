package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.MainApplication
import com.jeppeman.jetpackplayground.data.di.NetModule
import com.jeppeman.jetpackplayground.data.di.RepositoryModule
import com.jeppeman.jetpackplayground.di.androidx.AndroidXInjectionModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    NetModule::class,
    RepositoryModule::class,
    ActivityBuilder::class,
    AndroidInjectionModule::class,
    AndroidXInjectionModule::class
])
interface AppComponent : AndroidInjector<MainApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MainApplication>()
}