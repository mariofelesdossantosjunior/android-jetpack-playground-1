package com.jeppeman.jetpackplayground.ui

import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.base.BaseViewModelTest
import com.jeppeman.jetpackplayground.orientation.ScreenMode
import com.jeppeman.jetpackplayground.util.setProperty
import org.junit.Test

class ContainerViewModelTest : BaseViewModelTest<ContainerViewModel>() {
    override lateinit var viewModel: ContainerViewModel

    override fun before() {
        viewModel = ContainerViewModel()
    }

    @Test
    fun whenScreenModeIsNotFullscreenAndValueIsTrue_setLandscape_ShouldSetScreenModeToLandscape() {
        viewModel.setProperty("screenMode", ScreenMode.UNDEFINED)

        viewModel.landscape = true

        assertThat(viewModel.screenMode).isEqualTo(ScreenMode.LANDSCAPE)
    }

    @Test
    fun whenScreenModeIsNotUndefinedAndValueIsFalse_setLandscape_ShouldSetScreenModeToUndefined() {
        viewModel.setProperty("screenMode", ScreenMode.LANDSCAPE)

        viewModel.landscape = false

        assertThat(viewModel.screenMode).isEqualTo(ScreenMode.UNDEFINED)
    }

    @Test
    fun enterFullscreen_ShouldSetScreenModeToFullscreen() {
        viewModel.enterFullscreen()

        assertThat(viewModel.screenMode).isEqualTo(ScreenMode.FULLSCREEN)
    }

    @Test
    fun whenLandscapeIsTrue_exitFullscreen_ShouldSetScreenModeToLandscape() {
        viewModel.landscape = true

        viewModel.exitFullscreen()

        assertThat(viewModel.screenMode).isEqualTo(ScreenMode.LANDSCAPE)
    }

    @Test
    fun whenLandscapeIsFalse_exitFullscreen_ShouldSetScreenModeToUndefined() {
        viewModel.exitFullscreen()

        assertThat(viewModel.screenMode).isEqualTo(ScreenMode.UNDEFINED)
    }
}