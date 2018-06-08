package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.di.scopes.PerActivity
import com.jeppeman.jetpackplayground.ui.ContainerActivity
import com.jeppeman.jetpackplayground.ui.ContainerModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @PerActivity
    @ContributesAndroidInjector(modules = [ContainerModule::class, FragmentBuilder::class])
    abstract fun buildContainerActivity(): ContainerActivity
}