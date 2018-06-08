package com.jeppeman.jetpackplayground.ui.videodetail

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.util.getProperty
import com.jeppeman.jetpackplayground.util.invokeMethod
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.util.DataSource
import java.io.FileDescriptor
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class VideoDetailPlayerImplTest {
    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()
    private lateinit var videoDetailPlayer: VideoDetailPlayerImpl
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var context: Context
    private lateinit var handler: Handler
    private lateinit var testFile: FileDescriptor
    private lateinit var testDataSource: DataSource
    private lateinit var mediaInfo: ShadowMediaPlayer.MediaInfo

    private fun preparePlayer() {
        mediaPlayer.apply {
            setDataSource(testFile)
            prepareAsync()
        }
    }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        handler = Handler()
        videoDetailPlayer = VideoDetailPlayerImpl(context, handler)
        mediaPlayer = videoDetailPlayer.getProperty("mediaPlayer")
        testFile = context.resources.openRawResourceFd(R.raw.test_video).fileDescriptor
        testDataSource = DataSource.toDataSource(testFile)
        mediaInfo = ShadowMediaPlayer.MediaInfo()
        ShadowMediaPlayer.addMediaInfo(testDataSource, mediaInfo)
    }

    @Test
    fun setVideoUrl_ShouldCallPrepare() {
        exceptionRule.expect(IOException::class.java)

        videoDetailPlayer.videoUrl = "This url has no chance of working"

        verify(videoDetailPlayer).invokeMethod("prepare")
    }

    @Test
    fun mediaPlayerPrepared_shouldCallOnReadyListenersAndSetStateToReady() {
        var didInvokeCallback = false
        val onPlaybackReadyListener = { didInvokeCallback = true }
        videoDetailPlayer.registerPlaybackReadyListener(onPlaybackReadyListener)
        assertThat(videoDetailPlayer.isReady).isFalse()

        preparePlayer()

        assertThat(didInvokeCallback).isTrue()
        assertThat(videoDetailPlayer.isReady).isTrue()
    }

    @Test
    fun mediaPlayerCompleted_shouldCallOnCompletedListenersAndSetStateToNotPlaying() {
        var didInvokeCallback = false
        val onCompletedListener = { didInvokeCallback = true }
        videoDetailPlayer.registerCompletionListener(onCompletedListener)
        preparePlayer()

        videoDetailPlayer.start()
        assertThat(videoDetailPlayer.getProperty<Boolean>("isPlaying")).isTrue()
        mediaPlayer.seekTo(mediaPlayer.duration)

        assertThat(didInvokeCallback).isTrue()
        assertThat(videoDetailPlayer.getProperty<Boolean>("isPlaying")).isFalse()
    }

    @Test
    fun mediaPlayerError_shouldCallOnErrorListeners() {
        var didInvokeCallback = false
        val onErrorListener = { what: Int, extra: Int ->
            didInvokeCallback = true
            assertThat(what).isEqualTo(2)
            assertThat(extra).isEqualTo(3)
        }
        videoDetailPlayer.registerErrorListener(onErrorListener)
        preparePlayer()
        mediaInfo.scheduleErrorAtOffset(50, 2, 3)

        videoDetailPlayer.start()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertThat(didInvokeCallback).isTrue()
    }

    @Test
    fun start_ShouldStartMediaPlayerAndNotifyProgression() {
        var didInvokeCallback = false
        val progressListener = { _: Int -> didInvokeCallback = true }
        videoDetailPlayer.registerProgressListener(progressListener)
        preparePlayer()

        videoDetailPlayer.start()

        assertThat(mediaPlayer.isPlaying).isTrue()
        assertThat(didInvokeCallback).isTrue()
    }

    @Test
    fun pause_ShouldStopPlayback() {
        preparePlayer()

        videoDetailPlayer.start()
        assertThat(videoDetailPlayer.getProperty<Boolean>("isPlaying")).isTrue()
        assertThat(mediaPlayer.isPlaying).isTrue()
        videoDetailPlayer.pause()

        assertThat(videoDetailPlayer.getProperty<Boolean>("isPlaying")).isFalse()
        assertThat(mediaPlayer.isPlaying).isFalse()
    }

    @Test
    fun restart_ShouldResetToBeginning() {
        preparePlayer()

        videoDetailPlayer.start()
        mediaPlayer.seekTo(500)
        assertThat(mediaPlayer.currentPosition).isEqualTo(500)
        videoDetailPlayer.restart()

        assertThat(mediaPlayer.currentPosition).isEqualTo(0)
    }

    @Test
    fun fastForward_ShouldAdvancePlayer() {
        preparePlayer()

        videoDetailPlayer.start()
        val progressBefore = mediaPlayer.currentPosition
        videoDetailPlayer.fastForward()

        assertThat(progressBefore).isLessThan(mediaPlayer.currentPosition)
    }

    @Test
    fun rewind_ShouldRetractPlayer() {
        preparePlayer()

        videoDetailPlayer.start()
        videoDetailPlayer.fastForward()
        val progressBefore = mediaPlayer.currentPosition
        videoDetailPlayer.rewind()

        assertThat(progressBefore).isGreaterThan(mediaPlayer.currentPosition)
    }
}