package com.jeppeman.jetpackplayground.ui.videolist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.base.BaseViewModelTest
import com.jeppeman.jetpackplayground.domain.interactor.GetVideosUseCase
import com.jeppeman.jetpackplayground.domain.model.Video
import com.jeppeman.jetpackplayground.model.VideoModel
import com.jeppeman.jetpackplayground.model.mapper.VideoModelMapper
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@SmallTest
@RunWith(MockitoJUnitRunner::class)
class VideoListViewModelImplTest : BaseViewModelTest<VideoListViewModelImpl>() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    @Mock
    private lateinit var mockVideoModelMapper: VideoModelMapper
    @Mock
    private lateinit var mockGetVideosUseCase: GetVideosUseCase
    override lateinit var viewModel: VideoListViewModelImpl
    private lateinit var videoItemViewModels: List<VideoListItemViewModel>

    private val videoModels = listOf(
            VideoModel(
                    title = "Fun title :)",
                    subtitle = "Fun subtitle :)",
                    source = "https://fun-video.com",
                    thumb = "https://fun-thumb.com"
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

    override fun before() {
        viewModel = spy(VideoListViewModelImpl(
                getVideosUseCase = mockGetVideosUseCase.apply {
                    `when`(publish(any())).thenReturn(Single.just(listOf()))
                },
                videoModelMapper = mockVideoModelMapper
        ))
        videoItemViewModels =  listOf(VideoListItemViewModel(videoModels[0], viewModel))
    }

    @Test
    fun onInitialize_ShouldTriggerRefresh() {
        `when`(mockGetVideosUseCase.publish(any())).thenReturn(Single.just(listOf()))

        viewModel.onInitialize()

        verify(viewModel).refresh()
    }

    @Test
    fun refresh_ShouldGenerateLoadingViewModel() {
        val fakeSingle = Single.just(listOf<Video>()).subscribeOn(testScheduler)
        `when`(mockGetVideosUseCase.publish(any())).thenReturn(fakeSingle)

        viewModel.refresh()

        assertThat(viewModel.items.value?.first() is VideoListLoadingViewModel).isTrue()
    }

    @Test
    fun whenVideosAvailable_refresh_ShouldGenerateItemViewModels() {
        val fakeVideosSingle = Single.just(videos).subscribeOn(testScheduler)
        `when`(mockGetVideosUseCase.publish(any())).thenReturn(fakeVideosSingle)
        `when`(mockVideoModelMapper.transformToViewModel(videos, viewModel)).thenReturn(videoItemViewModels)

        viewModel.refresh()
        testScheduler.triggerActions()

        assertThat(viewModel.items.value?.first()).isEqualTo(videoItemViewModels[0])
    }

    @Test
    fun whenError_refresh_ShouldGenerateEmptyItemViewModel() {
        val fakeVideosSingle = Single.error<List<Video>>(Exception()).subscribeOn(testScheduler)
        `when`(mockGetVideosUseCase.publish(any())).thenReturn(fakeVideosSingle)

        viewModel.refresh()
        testScheduler.triggerActions()

        assertThat(viewModel.items.value?.first() is VideoListEmptyViewModel).isTrue()
    }
}