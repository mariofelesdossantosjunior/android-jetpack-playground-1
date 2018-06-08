package com.jeppeman.jetpackplayground.data.di

import android.content.Context
import com.jeppeman.jetpackplayground.data.R
import com.jeppeman.jetpackplayground.data.net.VideoApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetModule {
    @Provides
    @Singleton
    fun provideVideoApi(@VideoApiBaseUrl videoApiBaseUrl: String,
                        moshi: Moshi): Single<VideoApi> {
        return Single.fromCallable {
            Retrofit.Builder()
                    .baseUrl(videoApiBaseUrl)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(VideoApi::class.java)
        }
    }

    @Provides
    @Singleton
    @VideoApiBaseUrl
    fun provideVideoApiBaseUrl(context: Context): String {
        return context.getString(R.string.video_api_base_url)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }
}