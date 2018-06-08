package com.jeppeman.jetpackplayground.model.mapper

import com.jeppeman.jetpackplayground.domain.model.Video
import com.jeppeman.jetpackplayground.model.VideoModel
import com.jeppeman.jetpackplayground.ui.videolist.VideoListItemViewModel
import com.jeppeman.jetpackplayground.ui.videolist.VideoListViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoModelMapper @Inject constructor()
    : ModelMapper<VideoModel, Video> {
    fun transformToViewModel(from: List<Video>, parent: VideoListViewModel): List<VideoListItemViewModel> {
        return from.asSequence()
                .map(::toModel)
                .map { videoModels -> VideoListItemViewModel(videoModels, parent) }
                .toList()
    }

    override fun toModel(from: Video): VideoModel {
        return VideoModel(
                title = from.title,
                subtitle = from.subtitle,
                thumb = from.thumb,
                source = from.source
        )
    }

    override fun toDomain(from: VideoModel): Video {
        return Video(
                title = from.title,
                subtitle = from.subtitle,
                thumb = from.thumb,
                source = from.source
        )
    }
}