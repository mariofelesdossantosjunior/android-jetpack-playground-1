package com.jeppeman.jetpackplayground.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class PlayButton(context: Context, attributeSet: AttributeSet) : AppCompatImageButton(context, attributeSet) {
    private val behavior = PlayButtonAnimationBehavior(this)

    val state get() = behavior.state
    val isAnimating get() = behavior.isAnimating
    fun runPauseToPlay() = behavior.runPauseToPlay()
    fun runPlayToPause() = behavior.runPlayToPause()
    fun markCompleted() = behavior.markCompleted()
}