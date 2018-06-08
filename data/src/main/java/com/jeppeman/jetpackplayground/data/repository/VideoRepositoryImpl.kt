package com.jeppeman.jetpackplayground.data.repository

import com.jeppeman.jetpackplayground.data.entity.VideoCategoryEntity
import com.jeppeman.jetpackplayground.data.entity.mapper.VideoEntityMapper
import com.jeppeman.jetpackplayground.data.net.VideoApi
import com.jeppeman.jetpackplayground.domain.model.Video
import com.jeppeman.jetpackplayground.domain.repository.VideoRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
        private val videoApi: Single<VideoApi>,
        private val videoEntityMapper: VideoEntityMapper)
    : VideoRepository {

    override fun getVideos(): Single<List<Video>> {
        return videoApi.flatMap(VideoApi::getVideos)
                .map { response -> response.categories.flatMap(VideoCategoryEntity::videos) }
                .map(videoEntityMapper::toDomain)
    }
}