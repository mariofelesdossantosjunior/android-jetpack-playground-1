package com.jeppeman.jetpackplayground.ui.widget

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.jeppeman.jetpackplayground.R

class PlayButtonAnimationBehavior(
        private val target: ImageButton,
        private val replay: Drawable? = ContextCompat.getDrawable(target.context, R.drawable.ic_replay),
        private var playToPause: AnimatedVectorDrawable? =
                ContextCompat.getDrawable(target.context, R.drawable.ic_play_to_pause) as? AnimatedVectorDrawable,
        private var pauseToPlay: AnimatedVectorDrawable? =
                ContextCompat.getDrawable(target.context, R.drawable.ic_pause_to_play) as? AnimatedVectorDrawable) {

    var state = State.PAUSED
        private set
    private val playToPauseCallback = object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            isAnimating = false
            state = State.PLAYING
            target.setImageDrawable(pauseToPlay)
            playToPause?.unregisterAnimationCallback(this)
            playToPause?.stop()
            playToPause?.reset()
        }
    }
    private val pauseToPlayCallback = object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            isAnimating = false
            state = State.PAUSED
            target.setImageDrawable(playToPause)
            pauseToPlay?.unregisterAnimationCallback(this)
            pauseToPlay?.stop()
            pauseToPlay?.reset()
        }
    }
    var isAnimating = false
        private set

    init {
        target.setImageDrawable(playToPause)
    }

    fun markCompleted() {
        state = State.COMPLETED
        target.setImageDrawable(replay)
    }

    fun runPlayToPause() {
        if (state != State.PLAYING) {
            playToPause?.apply {
                isAnimating = true
                target.setImageDrawable(this)
                registerAnimationCallback(playToPauseCallback)
                start()
            }
        }
    }

    fun runPauseToPlay() {
        if (state == State.PLAYING) {
            pauseToPlay?.apply {
                isAnimating = true
                target.setImageDrawable(this)
                registerAnimationCallback(pauseToPlayCallback)
                start()
            }
        }
    }

    enum class State {
        PLAYING, PAUSED, COMPLETED;
    }
}