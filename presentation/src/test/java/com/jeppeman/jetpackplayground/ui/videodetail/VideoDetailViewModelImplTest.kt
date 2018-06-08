package com.jeppeman.jetpackplayground.ui.videodetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.BR
import com.jeppeman.jetpackplayground.base.BaseViewModelTest
import com.jeppeman.jetpackplayground.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.extensions.timeFormat
import com.jeppeman.jetpackplayground.model.VideoModel
import com.jeppeman.jetpackplayground.orientation.ScreenMode
import com.jeppeman.jetpackplayground.util.setProperty
import com.jeppeman.jetpackplayground.ui.ContainerViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class VideoDetailViewModelImplTest : BaseViewModelTest<VideoDetailViewModelImpl>() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    @Mock
    private lateinit var mockVideoDetailPlayer: VideoDetailPlayer
    @Mock
    private lateinit var mockContainerViewModel: ContainerViewModel
    @Mock
    private lateinit var mockStateContext: VideoDetailViewModel.StateContext
    private val videoModel = VideoModel(
            title = "Title",
            subtitle = "Subtitle",
            source = "http://source.com",
            thumb = "http://thumb.com"
    )
    override lateinit var viewModel: VideoDetailViewModelImpl

    override fun before() {
        `when`(mockStateContext.state).thenReturn(mutableLiveDataOf())
        `when`(mockContainerViewModel.observeScreenMode()).thenReturn(Observable.just(ScreenMode.LANDSCAPE))
        viewModel = spy(VideoDetailViewModelImpl(
                videoDetailPlayer = mockVideoDetailPlayer,
                containerViewModel = mockContainerViewModel,
                stateContext = mockStateContext,
                videoDetailParameter = VideoDetailParameter(videoModel)
        ))
    }

    @Test
    fun onInitialize_ShouldSetTitleInitializePlayerAndObserveScreenMode() {
        viewModel.onInitialize()

        assertThat(viewModel.video).isEqualTo(videoModel)
        assertThat(viewModel.title.value).isEqualTo(videoModel.title)
        verify(viewModel).resetState()
        verify(mockVideoDetailPlayer).videoUrl = videoModel.source
        verify(mockVideoDetailPlayer).registerCompletionListener(mockStateContext::onCompleted)
        verify(mockVideoDetailPlayer).registerErrorListener(mockStateContext::onError)
        verify(mockVideoDetailPlayer).registerPlaybackReadyListener(viewModel::onPlaybackReady)
        verify(mockVideoDetailPlayer).registerProgressListener(viewModel::onVideoProgress)
    }

    @Test
    fun whenLeaping_setProgress_ShouldNotifyPropertyChangeShowOverlayAndSetProgressOnPlayer() {
        val fakeProgress = 3

        viewModel.videoProgress = fakeProgress

        verify(viewModel).notifyPropertyChanged(BR.videoProgress)
        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).progress = fakeProgress
        assertThat(viewModel.currentProgressText.value).isEqualTo(fakeProgress.timeFormat())
    }

    @Test
    fun whenNotLeaping_setProgress_ShouldNotifyPropertyChangeNotShowOverlayNorSetProgressOnPlayer() {
        val fakeProgress = 3
        viewModel.setProperty("currentProgressFromPlayer", fakeProgress)

        viewModel.videoProgress = fakeProgress

        verify(viewModel).notifyPropertyChanged(BR.videoProgress)
        verify(viewModel, never()).showOverlay()
        verify(mockVideoDetailPlayer, never()).progress = fakeProgress
        assertThat(viewModel.currentProgressText.value).isEqualTo(fakeProgress.timeFormat())
    }

    @Test
    fun onPlaybackReady_ShouldSetVideoLengthAndDelegateToStateContext() {
        val fakeVideoLength = 500
        `when`(mockVideoDetailPlayer.duration).thenReturn(fakeVideoLength)

        viewModel.onPlaybackReady()

        assertThat(viewModel.videoLength.value).isEqualTo(fakeVideoLength)
        assertThat(viewModel.videoLengthText.value).isEqualTo(fakeVideoLength.timeFormat())
        verify(mockStateContext).onPlaybackReady()
    }

    @Test
    fun resetState_ShouldSetStateToInitState() {
        viewModel.resetState()

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.InitStateImpl::class.java)
    }

    @Test
    fun hideOverlay_ShouldSetOverlayVisibleToFalseAndDisposeCountDownDisposable() {
        val mockDisposable = mock(Disposable::class.java)
        viewModel.setProperty("overlayCountDownDisposable", mockDisposable)

        viewModel.hideOverlay()

        verify(mockDisposable).dispose()
        assertThat(viewModel.overlayVisible.value).isFalse()
    }

    @Test
    fun overlayCountDownFinished_ShouldHideOverlayAndEnterFullscreen() {
        viewModel.onOverlayCountDownFinished(0L)

        verify(viewModel).hideOverlay()
        verify(mockContainerViewModel).enterFullscreen()
    }

    @Test
    fun whenScreenModeIsLandscape_showOverlay_ShouldShowOverlayAndHideAgainAfterTimeout() {
        `when`(mockContainerViewModel.screenMode).thenReturn(ScreenMode.LANDSCAPE)
        val mockDisposable = mock(Disposable::class.java)
        viewModel.setProperty("overlayCountDownDisposable", mockDisposable)

        viewModel.showOverlay()

        assertThat(viewModel.overlayVisible.value).isTrue()
        verify(mockDisposable).dispose()
        testScheduler.advanceTimeBy(5L, TimeUnit.SECONDS)
        verify(viewModel).onOverlayCountDownFinished(0L)
    }

    @Test
    fun whenScreenModeIsNotLandscape_showOverlay_ShouldDoNothing() {
        `when`(mockContainerViewModel.screenMode).thenReturn(ScreenMode.UNDEFINED)
        val mockDisposable = mock(Disposable::class.java)
        viewModel.setProperty("overlayCountDownDisposable", mockDisposable)

        viewModel.showOverlay()

        verify(mockDisposable, never()).dispose()
        assertThat(viewModel.overlayVisible.value).isFalse()
    }

    @Test
    fun onCleared_ShouldHideOverlayAndReleaseMediaPlayerAndExitFullscreen() {
        viewModel.onCleared()

        verify(mockVideoDetailPlayer).release()
        verify(mockContainerViewModel).exitFullscreen()
        verify(viewModel).hideOverlay()
    }

    @Test
    fun onStop_ShouldDelegateToStateContext() {
        viewModel.onStop()

        verify(mockStateContext).onStop()
    }

    @Test
    fun onStart_ShouldDelegateToStateContext() {
        viewModel.onStart()

        verify(mockStateContext).onStart()
    }

    @Test
    fun onOverlayClick_ShouldShowOverlay() {
        viewModel.onOverlayClick()

        verify(viewModel).showOverlay()
    }

    @Test
    fun onPlayClick_ShouldShowOverlayAndDelegateToStateContext() {
        viewModel.onPlayClick()

        verify(viewModel).showOverlay()
        verify(mockStateContext).play()
    }

    @Test
    fun onCloseVideoClick_ShouldResetAllState() {
        viewModel.onCloseVideoClick()

        verify(viewModel).hideOverlay()
        verify(viewModel).resetState()
        verify(mockContainerViewModel).exitFullscreen()
        verify(mockVideoDetailPlayer).reset()
        assertThat(viewModel.fullscreen.value).isFalse()
        assertThat(viewModel.title.value).isEqualTo(videoModel.title)
    }

    @Test
    fun onRewindClick_ShouldShowOverlayAndDelegateToStatePlayer() {
        viewModel.onRewindClick()

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).rewind()
    }

    @Test
    fun onFastForwardClick_ShouldShowOverlayAndDelegateToStatePlayer() {
        viewModel.onFastForwardClick()

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).fastForward()
    }

    @Test
    fun whenSoundOn_onVolumeClick_ShouldShowOverlayAndMutePlayer() {
        viewModel.soundOn.value = true
        viewModel.onVolumeClick()

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).mute()
        assertThat(viewModel.soundOn.value).isFalse()
    }

    @Test
    fun whenSoundOff_onVolumeClick_ShouldShowOverlayAndUnmutePlayer() {
        viewModel.soundOn.value = false
        viewModel.onVolumeClick()

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).unmute()
        assertThat(viewModel.soundOn.value).isTrue()
    }

    @Test
    fun onLandscapeTransitionFinished_ShouldEnterFullscreenAndShowOverlay() {
        viewModel.onLandscapeTransitionFinished()

        verify(mockContainerViewModel).enterFullscreen()
        verify(viewModel).showOverlay()
    }

    @Test
    fun onLoadingAnimationFinished_ShouldDelegateToStateContext() {
        viewModel.onLoadingAnimationFinished()

        verify(mockStateContext).onLoadingAnimationFinished()
    }

    @Test
    fun whenInitStateAndVideoPlayerIsReady_play_ShouldSetStateToPlaying() {
        `when`(mockVideoDetailPlayer.isReady).thenReturn(true)

        viewModel.InitStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenInitStateAndVideoPlayerIsNotReady_play_ShouldSetStateToLoading() {
        `when`(mockVideoDetailPlayer.isReady).thenReturn(false)

        viewModel.InitStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.LoadingStateImpl::class.java)
    }

    @Test
    fun initPausedState_ShouldPausePlayer() {
        viewModel.PausedStateImpl()

        verify(mockVideoDetailPlayer).pause()
    }

    @Test
    fun whenPausedStateAndVideoPlayerIsReady_play_ShouldSetStateToPlaying() {
        `when`(mockVideoDetailPlayer.isReady).thenReturn(true)

        viewModel.PausedStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenPausedStateAndVideoPlayerIsNotReady_play_ShouldSetStateToLoading() {
        `when`(mockVideoDetailPlayer.isReady).thenReturn(false)

        viewModel.PausedStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.LoadingStateImpl::class.java)
    }

    @Test
    fun whenPausedState_onStart_ShouldSetStateToPlaying() {
        viewModel.PausedStateImpl().onStart(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenScreenModeIsLandscape_initPlayingState_ShouldStartPlayerAndEnterFullscreen() {
        `when`(mockContainerViewModel.screenMode).thenReturn(ScreenMode.LANDSCAPE)

        viewModel.PlayingStateImpl()

        verify(mockVideoDetailPlayer).start()
        assertThat(viewModel.fullscreen.value).isTrue()
        verify(mockContainerViewModel).enterFullscreen()
        verify(viewModel).showOverlay()
    }

    @Test
    fun whenScreenModeIsNotLandscape_initPlayingState_ShouldOnlyStartPlayer() {
        `when`(mockContainerViewModel.screenMode).thenReturn(ScreenMode.UNDEFINED)

        viewModel.PlayingStateImpl()

        verify(mockVideoDetailPlayer).start()
        assertThat(viewModel.fullscreen.value).isFalse()
        verify(mockContainerViewModel, never()).enterFullscreen()
        verify(viewModel, never()).showOverlay()
    }

    @Test
    fun whenPlayingState_onStop_ShouldSetStateToPaused() {
        viewModel.PlayingStateImpl().onStop(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PausedStateImpl::class.java)
    }

    @Test
    fun whenPlayingState_play_ShouldSetStateToPaused() {
        viewModel.PlayingStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PausedStateImpl::class.java)
    }

    @Test
    fun whenPlayingState_onCompleted_ShouldSetStateToCompleted() {
        viewModel.PlayingStateImpl().onCompleted(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.CompletedStateImpl::class.java)
    }

    @Test
    fun whenPlayingStateScreenModeIsLandscapeAndFullscreenIsFalse_onScreenModeChanged_ShouldEnterFullscreen() {
        viewModel.fullscreen.value = false
        viewModel.PlayingStateImpl().onScreenModeChanged(mockStateContext, ScreenMode.LANDSCAPE)

        assertThat(viewModel.fullscreen.value).isTrue()
        verify(mockContainerViewModel).enterFullscreen()
        verify(viewModel).showOverlay()
    }

    @Test
    fun whenPlayingStateScreenModeIsNotLandscapeAndFullscreenIsTrue_onScreenModeChanged_ShouldExitFullscreen() {
        viewModel.fullscreen.value = true
        viewModel.PlayingStateImpl().onScreenModeChanged(mockStateContext, ScreenMode.UNDEFINED)

        assertThat(viewModel.fullscreen.value).isFalse()
    }

    @Test
    fun whenLoadingState_onPlaybackReady_ShouldSetStateToLoadingFinished() {
        viewModel.LoadingStateImpl().onPlaybackReady(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.LoadingFinishedStateImpl::class.java)
    }

    @Test
    fun whenLoadingState_onError_ShouldSetStateToError() {
        viewModel.LoadingStateImpl().onError(mockStateContext, 0, 0)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.ErrorStateImpl::class.java)
    }

    @Test
    fun whenLoadingFinishedState_onLoadingAnimationFinished_ShouldSetStateToPlaying() {
        viewModel.LoadingFinishedStateImpl().onLoadingAnimationFinished(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenCompletedState_play_ShouldRestartPlayerAndSetStateToPlaying() {
        viewModel.CompletedStateImpl().play(mockStateContext)

        verify(mockVideoDetailPlayer).restart()
        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenErrorState_play_ShouldSetStateToLoadingAndResetPlayer() {
        viewModel.ErrorStateImpl().play(mockStateContext)

        verify(mockVideoDetailPlayer).reset()
        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.LoadingStateImpl::class.java)
    }
}