package com.jeppeman.jetpackplayground.ui.videodetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade
import androidx.transition.Transition
import com.jeppeman.jetpackplayground.di.ViewModelFactory
import com.jeppeman.jetpackplayground.di.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class VideoDetailModule {
    @Provides
    @IntoMap
    @ViewModelKey(VideoDetailViewModelImpl::class)
    fun provideVideoDetailViewModelIntoMap(videoDetailViewModelImpl: VideoDetailViewModelImpl): ViewModel {
        return videoDetailViewModelImpl
    }

    @Provides
    fun provideVideoDetailViewModel(
            videoDetailFragment: VideoDetailFragment,
            viewModelFactory: ViewModelFactory): VideoDetailViewModel {
        return ViewModelProviders.of(videoDetailFragment, viewModelFactory)[VideoDetailViewModelImpl::class.java]
    }

    @Provides
    fun provideVideoDetailParameter(videoDetailFragment: VideoDetailFragment): VideoDetailParameter {
        return VideoDetailFragmentArgs.fromBundle(videoDetailFragment.arguments).videoDetailParameter
    }

    @Provides
    fun provideTransition(): Transition = Fade()

    @Provides
    fun provideVideoDetailPlayer(videoDetailPlayerImpl: VideoDetailPlayerImpl): VideoDetailPlayer = videoDetailPlayerImpl
}