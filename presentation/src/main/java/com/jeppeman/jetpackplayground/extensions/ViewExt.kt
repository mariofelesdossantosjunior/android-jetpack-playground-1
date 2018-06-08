package com.jeppeman.jetpackplayground.extensions

import android.graphics.SurfaceTexture
import android.view.TextureView
import android.view.View
import android.view.ViewGroup

private fun findViewWithTransitionNameTraversal(view: View, transitionName: String): View? {
    if (view.transitionName == transitionName) {
        return view
    }

    if (view is ViewGroup) {
        return (0 until view.childCount)
                .asSequence()
                .map(view::getChildAt)
                .mapNotNull { child -> child.findViewWithTransitionName(transitionName) }
                .firstOrNull()
    }

    return null
}

fun View.findViewWithTransitionName(transitionName: String): View? {
    return findViewWithTransitionNameTraversal(this, transitionName)
}

inline fun TextureView.onSurfaceAvailable(crossinline onAvailable: (SurfaceTexture?) -> Unit) {
    this.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?) = true

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            onAvailable(surface)
        }
    }
}