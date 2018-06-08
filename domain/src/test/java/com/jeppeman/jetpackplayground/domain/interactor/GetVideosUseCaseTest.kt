package com.jeppeman.jetpackplayground.domain.interactor

import com.jeppeman.jetpackplayground.domain.model.Video
import com.jeppeman.jetpackplayground.domain.repository.VideoRepository
import io.reactivex.Single
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetVideosUseCaseTest : BaseUseCaseTest<GetVideosUseCase>() {
    @Mock
    private lateinit var videoRepository: VideoRepository
    override lateinit var useCase: GetVideosUseCase

    private val videos = listOf(
            Video(
                    title = "Fun title :)",
                    subtitle = "Fun subtitle :)",
                    source = "https://fun-video.com",
                    thumb = "https://fun-thumb.com"
            )
    )

    override fun init() {
        useCase = GetVideosUseCase(videoRepository)
    }

    @Test
    fun getVideos_ShouldPropagate() {
        `when`(videoRepository.getVideos()).thenReturn(Single.just(videos))

        useCase.publish()
                .test()
                .also { testScheduler.triggerActions() }
                .assertValue { videos -> videos.isNotEmpty() }
    }

    @Test
    fun whenFailed_getVideos_ShouldPropagateError() {
        val exception = Exception()
        `when`(videoRepository.getVideos()).thenReturn(Single.error(exception))

        useCase.publish()
                .test()
                .also { testScheduler.triggerActions() }
                .assertError(exception)
    }
}