package com.jeppeman.jetpackplayground.ui

import com.jeppeman.jetpackplayground.extensions.getValue
import com.jeppeman.jetpackplayground.extensions.setValue
import com.jeppeman.jetpackplayground.orientation.ScreenMode
import com.jeppeman.jetpackplayground.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class ContainerViewModel : BaseViewModel() {
    private val screenModeSubject = BehaviorSubject.createDefault(ScreenMode.UNDEFINED)
    internal var landscape = false
        set(value) {
            field = value
            if (field && screenMode != ScreenMode.FULLSCREEN) {
                screenMode = ScreenMode.LANDSCAPE
            } else if (!field && screenMode != ScreenMode.UNDEFINED) {
                screenMode = ScreenMode.UNDEFINED
            }
        }

    var screenMode: ScreenMode by screenModeSubject
        private set

    fun enterFullscreen() {
        screenMode = ScreenMode.FULLSCREEN
    }

    fun exitFullscreen() {
        screenMode = if (landscape) ScreenMode.LANDSCAPE else ScreenMode.UNDEFINED
    }

    fun observeScreenMode(): Observable<ScreenMode> = screenModeSubject
}