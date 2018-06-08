package com.jeppeman.jetpackplayground.ui.videolist

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.espresso.isVisibleToUser
import com.jeppeman.jetpackplayground.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.model.VideoModel
import com.jeppeman.jetpackplayground.navigation.NavigationRequest
import com.jeppeman.jetpackplayground.ui.base.BaseFragmentTest
import com.jeppeman.jetpackplayground.ui.base.BaseViewHolder
import com.jeppeman.jetpackplayground.ui.videodetail.VideoDetailParameter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class VideoListFragmentTest : BaseFragmentTest<VideoListFragment, VideoListViewModel>() {
    private lateinit var videos: List<VideoListItemViewModel>
    override val fragmentClass = VideoListFragment::class
    @Mock
    override lateinit var viewModel: VideoListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        videos = listOf(
                VideoListItemViewModel(
                        VideoModel(
                                title = "First title :)",
                                subtitle = "Fun subtitle :)",
                                source = "https://fun-video.com",
                                thumb = "https://fun-thumb.com"
                        ),
                        viewModel
                ),
                VideoListItemViewModel(
                        VideoModel(
                                title = "Second title :)",
                                subtitle = "Fun subtitle :)",
                                source = "https://fun-video.com",
                                thumb = "https://fun-thumb.com"
                        ),
                        viewModel
                )
        )
    }

    override fun onFragmentInstantiated(fragment: VideoListFragment) {
        fragment.viewModel = viewModel
        fragment.videoListAdapter = VideoListAdapter()
        `when`(viewModel.items).thenReturn(mutableLiveDataOf(listOf()))
    }

    @Test
    fun clickVideoItem_ShouldRequestNavigation() {
        launch {
            `when`(viewModel.items).thenReturn(mutableLiveDataOf(videos))
        }

        onView(withId(R.id.videoList)).check(matches(isVisibleToUser())).perform(scrollToPosition<BaseViewHolder>(0))
        onView(withText(videos[0].videoModel.title)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).requestNavigation(NavigationRequest.ListToDetail(VideoDetailParameter(videos[0].videoModel)))
    }

    @Test
    fun emptyDataItem_ShouldDisplayEmptyElement() {
        launch {
            `when`(viewModel.items).thenReturn(mutableLiveDataOf(listOf(VideoListEmptyViewModel(viewModel))))
        }

        onView(withId(R.id.videoList)).perform(scrollToPosition<BaseViewHolder>(0))
        onView(withId(R.id.emptyContainer)).check(matches(isVisibleToUser()))
    }

    @Test
    fun loadingDataItem_ShouldDisplayLoaderElement() {
        launch {
            `when`(viewModel.items).thenReturn(mutableLiveDataOf(listOf(VideoListLoadingViewModel())))
        }

        onView(withId(R.id.videoList)).perform(scrollToPosition<BaseViewHolder>(0))
        onView(withId(R.id.loader)).check(matches(isVisibleToUser()))
    }

    @Test
    fun videoDataItems_ShouldDisplayVideoElements() {
        launch {
            `when`(viewModel.items).thenReturn(mutableLiveDataOf(videos))
        }

        onView(withId(R.id.videoList)).check(matches(isVisibleToUser())).perform(scrollToPosition<BaseViewHolder>(0))
        onView(withText(videos[0].videoModel.title)).check(matches(isVisibleToUser()))
        onView(withText(videos[1].videoModel.title)).check(matches(isVisibleToUser()))
    }
}