package com.jeppeman.jetpackplayground.ui.videolist

import com.jeppeman.jetpackplayground.model.VideoModel
import com.jeppeman.jetpackplayground.navigation.NavigationRequest
import com.jeppeman.jetpackplayground.ui.videodetail.VideoDetailParameter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VideoListItemViewModelTest {

    @Mock
    private lateinit var mockVideoListViewModel: VideoListViewModel
    private lateinit var videoListItemViewModel: VideoListItemViewModel
    private val videoModel = VideoModel("", "", "", "", "")

    @Before
    fun test() {
        videoListItemViewModel = VideoListItemViewModel(videoModel, mockVideoListViewModel)
    }

    @Test
    fun onClick_ShouldRequestNavigation() {
        videoListItemViewModel.onClick()

        verify(mockVideoListViewModel).requestNavigation(
                NavigationRequest.ListToDetail(VideoDetailParameter(videoModel)))
    }
}