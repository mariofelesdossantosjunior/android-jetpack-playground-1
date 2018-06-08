package com.jeppeman.jetpackplayground.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.databinding.ActivityContainerBinding
import com.jeppeman.jetpackplayground.di.androidx.HasFragmentXInjector
import com.jeppeman.jetpackplayground.orientation.ScreenMode
import com.jeppeman.jetpackplayground.ui.base.BaseActivity
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject


class ContainerActivity : BaseActivity<ActivityContainerBinding, ContainerViewModel>(),
        HasFragmentXInjector {

    private val isLandscape get() = resources.getBoolean(R.bool.landscape)
    override val layoutRes: Int get() = R.layout.activity_container
    override val fragmentXInjector get() = dispatchingFragmentXInjector

    @Inject
    override lateinit var viewModel: ContainerViewModel
    @Inject
    lateinit var dispatchingFragmentXInjector: DispatchingAndroidInjector<Fragment>

    private fun onScreenOrientationChanged(screenMode: ScreenMode) {
        when (screenMode) {
            ScreenMode.FULLSCREEN -> {
                window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            else -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel.apply {
            landscape = isLandscape
            observeScreenMode().subscribe(::onScreenOrientationChanged)
        }
    }

    override fun onBindingCreated(binding: ActivityContainerBinding) {
        binding.viewModel = viewModel
    }
}
