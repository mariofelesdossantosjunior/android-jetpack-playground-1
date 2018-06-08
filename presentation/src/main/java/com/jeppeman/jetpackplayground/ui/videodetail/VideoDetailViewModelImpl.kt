package com.jeppeman.jetpackplayground.ui.videodetail

import com.jeppeman.jetpackplayground.BR
import com.jeppeman.jetpackplayground.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.extensions.timeFormat
import com.jeppeman.jetpackplayground.model.VideoModel
import com.jeppeman.jetpackplayground.orientation.ScreenMode
import com.jeppeman.jetpackplayground.ui.ContainerViewModel
import com.jeppeman.jetpackplayground.ui.base.BaseViewModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VideoDetailViewModelImpl @Inject constructor(
        val videoDetailParameter: VideoDetailParameter,
        val containerViewModel: ContainerViewModel,
        val stateContext: VideoDetailViewModel.StateContext,
        override var videoDetailPlayer: VideoDetailPlayer)
    : BaseViewModel(), VideoDetailViewModel {

    private var ignoreProgressUpdatesFromUi = false
    private var currentProgressFromPlayer = 0
    private var overlayCountDownDisposable: Disposable? = null

    override val video: VideoModel = videoDetailParameter.videoModel
    override val state get() = stateContext.state
    override val fullscreen = mutableLiveDataOf(false)
    override val overlayVisible = mutableLiveDataOf(false)
    override val soundOn = mutableLiveDataOf(true)
    override val title = mutableLiveDataOf("")
    override val videoLength = mutableLiveDataOf(0)
    override val currentProgressText = mutableLiveDataOf("")
    override val videoLengthText = mutableLiveDataOf("")
    override var videoProgress: Int = 0
        set(value) {
            if (field != value) {
                notifyPropertyChanged(BR.videoProgress)
            }

            if (value != currentProgressFromPlayer) {
                if (!ignoreProgressUpdatesFromUi) {
                    showOverlay()
                    videoDetailPlayer.progress = value
                } else {
                    ignoreProgressUpdatesFromUi = false
                }
            }

            currentProgressText.value = value.timeFormat()

            field = value
        }


    fun resetState() {
        stateContext.state.value = InitStateImpl()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onOverlayCountDownFinished(time: Long) {
        hideOverlay()
        containerViewModel.enterFullscreen()
    }

    fun onVideoProgress(progress: Int) {
        currentProgressFromPlayer = progress
        videoProgress = currentProgressFromPlayer
    }

    fun onPlaybackReady() {
        videoLength.value = videoDetailPlayer.duration
        videoLengthText.value = videoDetailPlayer.duration.timeFormat()
        stateContext.onPlaybackReady()
    }

    fun showOverlay() {
        if (containerViewModel.screenMode == ScreenMode.FULLSCREEN
                || containerViewModel.screenMode == ScreenMode.LANDSCAPE) {
            overlayVisible.value = true
            overlayCountDownDisposable?.dispose()
            overlayCountDownDisposable = Single.timer(5L, TimeUnit.SECONDS)
                    .observeOnPostExecution()
                    .subscribe(::onOverlayCountDownFinished)
        }
    }

    fun hideOverlay() {
        overlayVisible.value = false
        overlayCountDownDisposable?.dispose()
    }

    override fun onInitialize() {
        resetState()
        title.value = video.title
        videoDetailPlayer.apply {
            videoUrl = video.source
            registerPlaybackReadyListener(::onPlaybackReady)
            registerCompletionListener(stateContext::onCompleted)
            registerErrorListener(stateContext::onError)
            registerProgressListener(::onVideoProgress)
        }

        containerViewModel.observeScreenMode()
                .subscribe(stateContext::onScreenModeChanged)
                .disposeOnCleared()
    }

    override fun onCleared() {
        super.onCleared()
        hideOverlay()
        videoDetailPlayer.release()
        containerViewModel.exitFullscreen()
    }

    override fun onStop() {
        stateContext.onStop()
    }

    override fun onStart() {
        stateContext.onStart()
    }

    override fun onOverlayClick() {
        showOverlay()
    }

    override fun onPlayClick() {
        showOverlay()
        stateContext.play()
    }

    override fun onCloseVideoClick() {
        hideOverlay()
        resetState()
        containerViewModel.exitFullscreen()
        videoDetailPlayer.reset()
        fullscreen.value = false
        title.value = video.title
    }

    override fun onRewindClick() {
        showOverlay()
        videoDetailPlayer.rewind()
    }

    override fun onFastForwardClick() {
        showOverlay()
        videoDetailPlayer.fastForward()
    }

    override fun onFullscreenClick() {
        fullscreen.value = true
        title.value = " "
    }

    override fun onVolumeClick() {
        showOverlay()
        val newVal = !requireNotNull(soundOn.value)
        soundOn.value = newVal
        if (newVal) {
            videoDetailPlayer.unmute()
        } else {
            videoDetailPlayer.mute()
        }
    }

    override fun onLandscapeTransitionFinished() {
        ignoreProgressUpdatesFromUi = true
        containerViewModel.enterFullscreen()
        showOverlay()
    }

    override fun onLoadingAnimationFinished() {
        stateContext.onLoadingAnimationFinished()
    }

    inner class InitStateImpl : VideoDetailViewModel.InitState {
        override val videoVisible = false

        override fun play(context: VideoDetailViewModel.StateContext) {
            context.state.value = if (videoDetailPlayer.isReady) {
                PlayingStateImpl()
            } else {
                LoadingStateImpl()
            }
        }
    }

    inner class PausedStateImpl : VideoDetailViewModel.PausedState {
        override val subActionsTag: String? = null

        init {
            videoDetailPlayer.pause()
        }

        override fun onStart(context: VideoDetailViewModel.StateContext) {
            context.state.value = PlayingStateImpl(false)
        }

        override fun play(context: VideoDetailViewModel.StateContext) {
            context.state.value = if (videoDetailPlayer.isReady) {
                PlayingStateImpl(false)
            } else {
                LoadingStateImpl()
            }
        }
    }

    inner class PlayingStateImpl(override val initial: Boolean = true) : VideoDetailViewModel.PlayingState {
        override val subActionsTag: String? = null

        init {
            videoDetailPlayer.start()
            if (containerViewModel.screenMode == ScreenMode.LANDSCAPE) {
                enterFullscreen()
            }
        }

        private fun enterFullscreen() {
            fullscreen.value = true
            containerViewModel.enterFullscreen()
            showOverlay()
        }

        override fun onStop(context: VideoDetailViewModel.StateContext) {
            context.state.value = PausedStateImpl()
        }

        override fun play(context: VideoDetailViewModel.StateContext) {
            context.state.value = PausedStateImpl()
        }

        override fun onCompleted(context: VideoDetailViewModel.StateContext) {
            context.state.value = CompletedStateImpl()
        }

        override fun onScreenModeChanged(context: VideoDetailViewModel.StateContext, mode: ScreenMode) {
            if (mode == ScreenMode.LANDSCAPE && fullscreen.value != true) {
                enterFullscreen()
            } else if (mode != ScreenMode.LANDSCAPE
                    && mode != ScreenMode.FULLSCREEN
                    && fullscreen.value == true) {
                fullscreen.value = false
            }
        }
    }

    inner class LoadingStateImpl : VideoDetailViewModel.LoadingState {
        override fun onPlaybackReady(context: VideoDetailViewModel.StateContext) {
            context.state.value = LoadingFinishedStateImpl()
        }

        override fun onError(context: VideoDetailViewModel.StateContext, what: Int, extra: Int) {
            context.state.value = ErrorStateImpl()
        }
    }

    inner class LoadingFinishedStateImpl : VideoDetailViewModel.LoadingFinishedState {
        override fun onLoadingAnimationFinished(context: VideoDetailViewModel.StateContext) {
            context.state.value = PlayingStateImpl()
        }
    }

    inner class CompletedStateImpl : VideoDetailViewModel.CompletedState {
        override val subActionsTag: String? = null

        override fun play(context: VideoDetailViewModel.StateContext) {
            videoDetailPlayer.restart()
            context.state.value = PlayingStateImpl(false)
        }
    }

    inner class ErrorStateImpl : VideoDetailViewModel.ErrorState {
        override fun play(context: VideoDetailViewModel.StateContext) {
            context.state.value = LoadingStateImpl()
            videoDetailPlayer.reset()
        }
    }
}