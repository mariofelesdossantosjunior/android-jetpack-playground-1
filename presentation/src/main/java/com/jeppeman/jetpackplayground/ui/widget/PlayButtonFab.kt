package com.jeppeman.jetpackplayground.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jeppeman.jetpackplayground.util.invokeMethod
import com.jeppeman.jetpackplayground.util.setProperty

/**
 * Wrapper to {@link FloatingActionButton}, this enables animating the background tint with
 * {@link MotionLayout}
 */
class PlayButtonFab(context: Context, attributeSet: AttributeSet) : FloatingActionButton(context, attributeSet) {

    private val behavior = PlayButtonAnimationBehavior(this)

    /**
     * Solves an issue where a new drawable would not be visible if applied to the button after
     * having been hidden once
     */
    private fun resetInternalImageMatrixScale() {
        invokeMethod<Any>("getImpl").setProperty("imageMatrixScale", 1.0f)
    }

    var backgroundTint: Int = Color.TRANSPARENT
        set(value) {
            field = value
            backgroundTintList = ColorStateList.valueOf(field)
        }

    val state get() = behavior.state
    val isAnimating get() = behavior.isAnimating

    fun runPauseToPlay() {
        resetInternalImageMatrixScale()
        behavior.runPauseToPlay()
    }

    fun runPlayToPause() {
        resetInternalImageMatrixScale()
        behavior.runPlayToPause()
    }

    fun markCompleted() = behavior.markCompleted()
}