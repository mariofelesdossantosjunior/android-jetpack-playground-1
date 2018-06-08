package com.jeppeman.jetpackplayground.data.net

import com.jeppeman.jetpackplayground.data.entity.reponse.VideoResponseEntity
import io.reactivex.Single
import retrofit2.http.GET

interface VideoApi {
    @GET("videos-enhanced-c.json")
    fun getVideos(): Single<VideoResponseEntity>
}