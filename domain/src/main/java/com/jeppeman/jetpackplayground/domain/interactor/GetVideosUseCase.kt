package com.jeppeman.jetpackplayground.domain.interactor

import com.jeppeman.jetpackplayground.domain.interactor.base.SingleUseCase
import com.jeppeman.jetpackplayground.domain.model.Video
import com.jeppeman.jetpackplayground.domain.repository.VideoRepository
import io.reactivex.Single
import javax.inject.Inject

class GetVideosUseCase @Inject constructor(val videoRepository: VideoRepository)
    : SingleUseCase<List<Video>, Unit>() {
    override fun buildUseCaseSingle(params: Unit?): Single<List<Video>> {
        return videoRepository.getVideos()
    }
}