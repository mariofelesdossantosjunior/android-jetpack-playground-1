package com.jeppeman.jetpackplayground.data.repository

import com.jeppeman.jetpackplayground.data.entity.VideoCategoryEntity
import com.jeppeman.jetpackplayground.data.entity.VideoEntity
import com.jeppeman.jetpackplayground.data.entity.mapper.VideoEntityMapper
import com.jeppeman.jetpackplayground.data.entity.reponse.VideoResponseEntity
import com.jeppeman.jetpackplayground.data.net.VideoApi
import com.jeppeman.jetpackplayground.domain.model.Video
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VideoRepositoryImplTest {
    @Mock
    private lateinit var videoApi: VideoApi
    @Mock
    private lateinit var videoEntityMapper: VideoEntityMapper

    private lateinit var videoRepositoryImpl: VideoRepositoryImpl

    private val videoResponseEntity = VideoResponseEntity(
            categories = listOf(
                    VideoCategoryEntity(
                            name = "Fake videos",
                            videos = listOf(
                                    VideoEntity(
                                            title = "Fun title :)",
                                            subtitle = "Fun subtitle :)",
                                            sources = listOf("https://fun-video.com"),
                                            thumb = "https://fun-thumb.com"
                                    )
                            )
                    )
            )
    )

    private val videos = listOf(
            Video(
                    title = "Fun title :)",
                    subtitle = "Fun subtitle :)",
                    source = "https://fun-video.com",
                    thumb = "https://fun-thumb.com"
            )
    )

    @Before
    fun setUp() {
        videoRepositoryImpl = VideoRepositoryImpl(Single.just(videoApi), videoEntityMapper)
        `when`(videoEntityMapper.toDomain(videoResponseEntity.categories[0].videos)).thenReturn(videos)
    }

    @Test
    fun getVideos_ShouldMapToDomainAndPropagate() {
        `when`(videoApi.getVideos()).thenReturn(Single.just(videoResponseEntity))

        videoRepositoryImpl.getVideos()
                .test()
                .assertValue(List<Video>::isNotEmpty)
    }

    @Test
    fun whenFailed_getVideos_ShouldPropagateError() {
        val exception = Exception()
        `when`(videoApi.getVideos()).thenReturn(Single.error(exception))

        videoRepositoryImpl.getVideos()
                .test()
                .assertError(exception)
    }
}