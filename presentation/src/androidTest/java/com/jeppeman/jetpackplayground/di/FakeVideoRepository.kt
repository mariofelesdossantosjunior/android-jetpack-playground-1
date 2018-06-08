package com.jeppeman.jetpackplayground.di

import com.jeppeman.jetpackplayground.domain.model.Video
import com.jeppeman.jetpackplayground.domain.repository.VideoRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeVideoRepository @Inject constructor() : VideoRepository {
    override fun getVideos(): Single<List<Video>> {
        return Single.just(videos)
    }

    companion object {
        val videos = listOf(
                Video(
                        title = "First title :)",
                        subtitle = "Fun subtitle :)",
                        source = "https://fun-video.com",
                        thumb = "https://fun-thumb.com"
                )
                ,
                Video(
                        title = "Second title :)",
                        subtitle = "Fun subtitle :)",
                        source = "https://fun-video.com",
                        thumb = "https://fun-thumb.com"
                ),
                Video(
                        title = "Second third :)",
                        subtitle = "Fun subtitle :)",
                        source = "https://fun-video.com",
                        thumb = "https://fun-thumb.com"
                )
        )
    }
}