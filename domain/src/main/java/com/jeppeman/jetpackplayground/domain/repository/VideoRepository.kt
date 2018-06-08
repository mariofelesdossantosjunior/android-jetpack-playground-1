package com.jeppeman.jetpackplayground.domain.repository

import com.jeppeman.jetpackplayground.domain.model.Video
import io.reactivex.Single

interface VideoRepository {
    fun getVideos(): Single<List<Video>>
}