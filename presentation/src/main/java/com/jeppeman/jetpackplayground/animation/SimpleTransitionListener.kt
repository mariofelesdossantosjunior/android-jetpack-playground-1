package com.jeppeman.jetpackplayground.animation

import androidx.constraintlayout.motion.widget.MotionLayout

class SimpleTransitionListener(
        private val onChange: (MotionLayout?, Int, Int, Float) -> Unit = { _, _, _, _ -> },
        private val onComplete: (MotionLayout?, Int) -> Unit = { _, _ -> })
    : MotionLayout.TransitionListener {
    override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
        onChange(layout, startId, endId, progress)
    }

    override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
        onComplete(layout, currentId)
    }
}