package com.jeppeman.jetpackplayground.ui.videodetail

import androidx.fragment.app.FragmentActivity
import androidx.test.espresso.IdlingResource
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import kotlinx.android.synthetic.main.fragment_video_detail.*

class VideoDetailAnimationIdlingResource : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null
    private var wasIdle = false

    override fun getName() = "VideoDetailAnimationIdlingResource"

    override fun isIdleNow(): Boolean {
        val fragment = (ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
                .firstOrNull() as FragmentActivity?)
                ?.supportFragmentManager
                ?.fragments
                ?.asSequence()
                ?.filterIsInstance(VideoDetailFragment::class.java)
                ?.firstOrNull() ?: return true

        val mainActionIsAnimating = fragment.mainAction?.isAnimating
        val fullscreenPlayIsAnimating = fragment.fullScreenPlay?.isAnimating
        val volumeButtonIsAnimating = fragment.fullScreenVolume?.isAnimating
        val arcViewAnimating = fragment.arc?.isAnimating
        val idle = mainActionIsAnimating != true
                && fullscreenPlayIsAnimating != true
                && volumeButtonIsAnimating != true
                && arcViewAnimating != true

        if (idle) {
            wasIdle = true
            resourceCallback?.onTransitionToIdle()
        }

        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}