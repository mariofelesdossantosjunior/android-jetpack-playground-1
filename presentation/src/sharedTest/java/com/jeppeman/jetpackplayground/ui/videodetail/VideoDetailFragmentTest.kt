package com.jeppeman.jetpackplayground.ui.videodetail

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.espresso.hasAnimationState
import com.jeppeman.jetpackplayground.espresso.hasSoundState
import com.jeppeman.jetpackplayground.espresso.hasTitle
import com.jeppeman.jetpackplayground.espresso.isLoading
import com.jeppeman.jetpackplayground.espresso.isVisibleToUser
import com.jeppeman.jetpackplayground.espresso.seekTo
import com.jeppeman.jetpackplayground.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.ui.base.BaseFragmentTest
import com.jeppeman.jetpackplayground.ui.widget.PlayButtonAnimationBehavior
import com.jeppeman.jetpackplayground.ui.widget.ToggleSoundButton
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class VideoDetailFragmentTest : BaseFragmentTest<VideoDetailFragment, VideoDetailViewModel>() {
    private lateinit var idlingResource: VideoDetailAnimationIdlingResource
    @Mock
    private lateinit var mockState: VideoDetailViewModel.State
    @Mock
    private lateinit var mockInitState: VideoDetailViewModel.InitState
    @Mock
    private lateinit var mockPausedState: VideoDetailViewModel.PausedState
    @Mock
    private lateinit var mockPlayingState: VideoDetailViewModel.PlayingState
    @Mock
    private lateinit var mockLoadingState: VideoDetailViewModel.LoadingState
    @Mock
    private lateinit var mockLoadingFinishedState: VideoDetailViewModel.LoadingFinishedState
    @Mock
    private lateinit var mockCompletedState: VideoDetailViewModel.CompletedState
    @Mock
    private lateinit var mockErrorState: VideoDetailViewModel.ErrorState
    @Mock
    private lateinit var mockVideoDetailPlayer: VideoDetailPlayer
    @Mock
    override lateinit var viewModel: VideoDetailViewModel
    override val fragmentClass = VideoDetailFragment::class

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        idlingResource = VideoDetailAnimationIdlingResource()
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    private fun launchInFullscreen(
            showOverlay: Boolean = true,
            onInstantiated: (VideoDetailFragment) -> Unit = {}
    ): FragmentScenario<VideoDetailFragment> {
        return launchInLandscape { fragment ->
            `when`(viewModel.fullscreen).thenReturn(mutableLiveDataOf(true))
            `when`(viewModel.overlayVisible).thenReturn(mutableLiveDataOf(showOverlay))
            onInstantiated(fragment)
        }
    }

    override fun onFragmentInstantiated(fragment: VideoDetailFragment) {
        viewModel.apply {
            `when`(videoDetailPlayer).thenReturn(mockVideoDetailPlayer)
            `when`(title).thenReturn(mutableLiveDataOf(""))
            `when`(state).thenReturn(mutableLiveDataOf(mockState))
            `when`(soundOn).thenReturn(mutableLiveDataOf(false))
            `when`(overlayVisible).thenReturn(mutableLiveDataOf(false))
            `when`(fullscreen).thenReturn(mutableLiveDataOf(false))
            fragment.viewModel = this
        }
    }

    @Test
    fun whenNotFullscreen_initState_ShouldSetPausedPlayButton() {
        launch {
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockInitState))
        }

        onView(withId(R.id.mainAction))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PAUSED)))
    }

    @Test
    fun whenFullscreen_initState_ShouldSetPausedPlayButton() {
        launchInFullscreen {
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockInitState))
        }

        onView(withId(R.id.fullScreenPlay))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PAUSED)))
    }

    @Test
    fun whenNotFullscreen_pausedState_ShouldSetPausedPlayButton() {
        launch {
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPausedState))
        }

        onView(withId(R.id.mainAction))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PAUSED)))
    }

    @Test
    fun whenFullscreen_pausedState_ShouldSetPausedPlayButton() {
        launchInFullscreen {
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPausedState))
        }

        onView(withId(R.id.fullScreenPlay))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PAUSED)))
    }

    @Test
    fun whenInitial_playingState_ShouldShowVideoActionsAndSetPlayButtonToPlayingState() {
        launch {
            `when`(mockPlayingState.initial).thenReturn(true)
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        listOf(
                R.id.close,
                R.id.rewind,
                R.id.mainAction,
                R.id.fastForward,
                R.id.fullScreen
        ).forEach { id ->
            onView(withId(id)).check(matches(isVisibleToUser())).perform(click())
        }

        onView(withId(R.id.mainAction))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PLAYING)))
    }

    @Test
    fun whenNotInitial_playingState_ShouldSetPlayButtonToPlayingState() {
        launch {
            `when`(mockPlayingState.initial).thenReturn(false)
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.mainAction))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PLAYING)))
    }

    @Test
    fun loadingState_ShouldShowLoader() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        launch {
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockLoadingState))
        }

        onView(withId(R.id.arc))
                .check(matches(isVisibleToUser()))
                .check(matches(isLoading()))

        IdlingRegistry.getInstance().register(idlingResource)
    }

    @Test
    fun loadingFinishedState_ShouldHideLoaderAndNotifyViewModel() {
        var stateLiveData: MutableLiveData<VideoDetailViewModel.State>? = null
        launch {
            stateLiveData = mutableLiveDataOf(mockLoadingState)
            `when`(viewModel.state).thenReturn(stateLiveData)
        }.onFragment {
            stateLiveData?.value = mockLoadingFinishedState
        }

        onView(withId(R.id.arc))
                .check(matches(not(isVisibleToUser())))
                .check(matches(not(isLoading())))
        verify(viewModel).onLoadingAnimationFinished()
    }

    @Test
    fun whenNotFullscreen_completedState_ShouldSetCompletedStateOnPlayButton() {
        launch {
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockCompletedState))
        }

        onView(withId(R.id.mainAction))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.COMPLETED)))
    }

    @Test
    fun whenFullscreen_completedState_ShouldSetCompletedStateOnPlayButton() {
        launchInFullscreen {
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockCompletedState))
        }

        onView(withId(R.id.fullScreenPlay))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.COMPLETED)))
    }

    @Test
    fun errorState_ShouldShowSnackbar() {
        launch {
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockErrorState))
        }

        onView(withText(R.string.video_detail_dismiss_snackbar)).check(matches(isVisibleToUser()))
        onView(withText(R.string.video_detail_video_failed_to_load)).check(matches(isVisibleToUser()))
    }

    @Test
    fun whenTrue_fullscreen_ShouldHideActionsAndContentAndNotifyViewModelOnCompletion() {
        launch {
            `when`(viewModel.fullscreen).thenReturn(mutableLiveDataOf(true))
        }

        listOf(
                R.id.mainAction,
                R.id.close,
                R.id.rewind,
                R.id.fastForward,
                R.id.fullScreen,
                R.id.videoDescription
        ).forEach { id ->
            onView(withId(id)).check(matches(not(isVisibleToUser())))
        }

        verify(viewModel).onLandscapeTransitionFinished()
    }


    @Test
    fun whenTrue_toggleOverlay_ShouldShowVideoOverlay() {
        launchInFullscreen()

        listOf(
                R.id.videoProgress,
                R.id.currentProgressText,
                R.id.videoLengthText,
                R.id.fullScreenClose,
                R.id.fullScreenRewind,
                R.id.fullScreenPlay,
                R.id.fullScreenFastForward,
                R.id.fullScreenVolume
        ).forEach { id ->
            onView(withId(id)).check(matches(isVisibleToUser())).perform(click())
        }
    }

    @Test
    fun whenFalse_toggleOverlay_ShouldHideVideoOverlay() {
        launchInFullscreen(false)

        listOf(
                R.id.videoProgress,
                R.id.currentProgressText,
                R.id.videoLengthText,
                R.id.fullScreenClose,
                R.id.fullScreenRewind,
                R.id.fullScreenPlay,
                R.id.fullScreenFastForward,
                R.id.fullScreenVolume
        ).forEach { id ->
            onView(withId(id)).check(matches(not(isVisibleToUser())))
        }
    }

    @Test
    fun setTitle_ShouldSetTitleOnCollapsingToolbar() {
        val title = "Title"
        launch {
            `when`(viewModel.title).thenReturn(mutableLiveDataOf(title))
        }

        onView(withId(R.id.collapsingToolbar))
                .check(matches(isVisibleToUser()))
                .check(matches(hasTitle(title)))
    }

    @Test
    fun whenSoundOn_toggleSoundButton_ShouldSetButtonStateToOff() {
        var soundLiveData: MutableLiveData<Boolean>? = null
        launchInFullscreen {
            soundLiveData = mutableLiveDataOf(true)
            `when`(viewModel.soundOn).thenReturn(soundLiveData)
        }.onFragment {
            soundLiveData?.value = false
        }

        onView(withId(R.id.fullScreenVolume))
                .check(matches(isVisibleToUser()))
                .check(matches(hasSoundState(ToggleSoundButton.SoundState.OFF)))
    }

    @Test
    fun whenSoundOff_toggleSoundButton_ShouldSetButtonStateToOn() {
        var soundLiveData: MutableLiveData<Boolean>? = null
        launchInFullscreen {
            soundLiveData = mutableLiveDataOf(false)
            `when`(viewModel.soundOn).thenReturn(soundLiveData)
        }.onFragment {
            soundLiveData?.value = true
        }

        onView(withId(R.id.fullScreenVolume))
                .check(matches(isVisibleToUser()))
                .check(matches(hasSoundState(ToggleSoundButton.SoundState.ON)))
    }

    @Test
    fun clickPlay_ShouldDelegateToViewModel() {
        launch()

        onView(withId(R.id.mainAction)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onPlayClick()
    }

    @Test
    fun clickFullscreenPlay_ShouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenPlay)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onPlayClick()
    }

    @Test
    fun clickCloseVideo_ShouldDelegateToViewModel() {
        launch {
            `when`(mockPlayingState.initial).thenReturn(true)
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.close)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onCloseVideoClick()
    }

    @Test
    fun clickCloseVideoFullscreen_ShouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenClose)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onCloseVideoClick()
    }

    @Test
    fun clickRewind_ShouldDelegateToViewModel() {
        launch {
            `when`(mockPlayingState.initial).thenReturn(true)
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.rewind)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onRewindClick()
    }

    @Test
    fun clickRewindFullscreen_ShouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenRewind)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onRewindClick()
    }

    @Test
    fun clickFastForward_ShouldDelegateToViewModel() {
        launch {
            `when`(mockPlayingState.initial).thenReturn(true)
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.fastForward)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onFastForwardClick()
    }

    @Test
    fun clickFastForwardFullscreen_ShouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenFastForward)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onFastForwardClick()
    }

    @Test
    fun clickFullscreen_ShouldDelegateToViewModel() {
        launch {
            `when`(mockPlayingState.initial).thenReturn(true)
            `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.fullScreen)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onFullscreenClick()
    }

    @Test
    fun clickVolume_ShouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenVolume)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onVolumeClick()
    }

    @Test
    fun changeVideoProgress_ShouldNotifyViewModel() {
        val progress = 50
        launchInFullscreen {
            `when`(viewModel.videoLength).thenReturn(mutableLiveDataOf(100))
        }

        onView(withId(R.id.videoProgress))
                .check(matches(isVisibleToUser()))
                .perform(seekTo(progress))

        verify(viewModel).videoProgress = progress
    }
}