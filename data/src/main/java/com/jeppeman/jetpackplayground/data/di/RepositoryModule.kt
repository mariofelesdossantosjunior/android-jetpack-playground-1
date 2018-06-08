package com.jeppeman.jetpackplayground.data.di

import com.jeppeman.jetpackplayground.data.repository.VideoRepositoryImpl
import com.jeppeman.jetpackplayground.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindVideoRepository(videoRepositoryImpl: VideoRepositoryImpl): VideoRepository
}