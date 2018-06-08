package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.FakeApplication
import com.jeppeman.jetpackplayground.di.androidx.AndroidXInjectionModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    FakeAppModule::class,
    ActivityBuilder::class,
    AndroidInjectionModule::class,
    AndroidXInjectionModule::class
])
interface FakeAppComponent : AndroidInjector<FakeApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<FakeApplication>()
}