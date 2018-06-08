package com.jeppeman.jetpackplayground.ui.videodetail

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.view.Surface
import androidx.core.net.toUri
import javax.inject.Inject

/**
 * Wrapper for a {@link MediaPlayer}
 */
class VideoDetailPlayerImpl @Inject constructor(
        private val context: Context,
        private val handler: Handler) : VideoDetailPlayer {

    private var isPlaying = false
    private val progressListeners = mutableListOf<(Int) -> Unit>()
    private val playbackReadyListeners = mutableListOf<() -> Unit>()
    private val completedListeners = mutableListOf<() -> Unit>()
    private val errorListeners = mutableListOf<(Int, Int) -> Unit>()
    private val mediaPlayer = MediaPlayer().apply {
        setOnCompletionListener {
            this@VideoDetailPlayerImpl.isPlaying = false
            completedListeners.forEach { onCompletedListener -> onCompletedListener() }
        }
        setOnPreparedListener {
            isReady = true
            playbackReadyListeners.forEach { onReadyListener -> onReadyListener() }
        }
        setOnErrorListener { _, what, extra ->
            errorListeners.forEach { errorListener -> errorListener(what, extra) }
            return@setOnErrorListener true
        }
    }
    override var videoUrl: String = ""
        set(value) {
            field = value
            prepare()
        }

    override var isReady = false
        private set

    override var progress: Int = 0
        set(value) {
            field = value
            mediaPlayer.seekTo(value)
        }

    override val duration get() = mediaPlayer.duration

    private fun prepare() {
        isReady = false
        mediaPlayer.apply {
            setDataSource(context, videoUrl.toUri())
            setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            prepareAsync()
        }
    }

    private fun progressTick() {
        if (!isPlaying) {
            return
        }

        progressListeners.forEach { listener ->
            listener(mediaPlayer.currentPosition)
        }

        handler.postDelayed(::progressTick, 30)
    }

    override fun mute() {
        mediaPlayer.setVolume(0f, 0f)
    }

    override fun unmute() {
        mediaPlayer.setVolume(1f, 1f)
    }

    override fun registerProgressListener(onProgressListener: (Int) -> Unit) {
        progressListeners.add(onProgressListener)
    }

    override fun registerErrorListener(onErrorListener: (Int, Int) -> Unit) {
        errorListeners.add(onErrorListener)
    }

    override fun registerPlaybackReadyListener(onPlaybackReadyListener: () -> Unit) {
        playbackReadyListeners.add(onPlaybackReadyListener)
    }

    override fun registerCompletionListener(onCompletedListener: () -> Unit) {
        completedListeners.add(onCompletedListener)
    }

    override fun rewind() {
        mediaPlayer.seekTo(Math.max(0, (mediaPlayer.currentPosition - mediaPlayer.duration * 0.1).toInt()))
    }

    override fun fastForward() {
        mediaPlayer.seekTo(Math.min(mediaPlayer.duration, (mediaPlayer.currentPosition + mediaPlayer.duration * 0.1).toInt()))
    }

    override fun pause() {
        isPlaying = false
        mediaPlayer.pause()
    }

    override fun start() {
        isPlaying = true
        mediaPlayer.start()
        progressTick()
    }

    override fun restart() {
        mediaPlayer.seekTo(0)
    }

    override fun reset() {
        isPlaying = false
        mediaPlayer.reset()
        prepare()
    }

    override fun release() {
        isPlaying = false
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.apply {
            stop()
            release()
        }
    }

    override fun attachSurface(surface: Surface) {
        mediaPlayer.setSurface(surface)
    }
}