package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.di.scopes.PerFragment
import com.jeppeman.jetpackplayground.ui.videodetail.VideoDetailFragment
import com.jeppeman.jetpackplayground.ui.videodetail.VideoDetailModule
import com.jeppeman.jetpackplayground.ui.videolist.VideoListFragment
import com.jeppeman.jetpackplayground.ui.videolist.VideoListModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {
    @PerFragment
    @ContributesAndroidInjector(modules = [VideoListModule::class])
    abstract fun buildVideoListFragment(): VideoListFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [VideoDetailModule::class])
    abstract fun buildVideoDetailFragment(): VideoDetailFragment
}