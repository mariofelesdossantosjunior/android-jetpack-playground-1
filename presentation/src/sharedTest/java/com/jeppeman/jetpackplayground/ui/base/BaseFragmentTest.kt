package com.jeppeman.jetpackplayground.ui.base

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.jeppeman.jetpackplayground.R
import org.junit.Test
import org.mockito.Mockito.verify
import kotlin.reflect.KClass

abstract class BaseFragmentTest<TFragment : BaseFragment<*, *>, TViewModel : IViewModel> {

    protected abstract val fragmentClass: KClass<TFragment>
    protected abstract val viewModel: TViewModel
    protected lateinit var fragment: TFragment
        private set

    protected abstract fun onFragmentInstantiated(fragment: TFragment)

    protected open fun launch(onInstantiated: (TFragment) -> Unit = {})
            : FragmentScenario<TFragment> {
        return FragmentScenario.launchInContainer(
                fragmentClass.java,
                null,
                R.style.AppTheme,
                object : FragmentFactory() {
                    @Suppress("UNCHECKED_CAST")
                    override fun instantiate(classLoader: ClassLoader, className: String, args: Bundle?): Fragment {
                        val fragment = super.instantiate(classLoader, className, args) as TFragment
                        this@BaseFragmentTest.fragment = fragment
                        onFragmentInstantiated(fragment)
                        onInstantiated(fragment)
                        return fragment
                    }
                }
        ).onFragment { fragment ->
            fragment.activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }.apply {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }
    }

    protected open fun launchInLandscape(onInstantiated: (TFragment) -> Unit = {})
            : FragmentScenario<TFragment> {
        // Needed to launch in landscape with Robolectric
        ApplicationProvider.getApplicationContext<Application>().apply {
            resources.apply {
                val dm = DisplayMetrics()
                configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
                updateConfiguration(configuration, dm)
                Resources.getSystem().updateConfiguration(configuration, dm)
                createConfigurationContext(configuration)
            }
        }
        return launch(onInstantiated).onFragment { fragment ->
            fragment.activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }.apply {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }
    }

    @Test
    fun onDestroy_ShouldUnregisterNavigationListener() {
        launch().apply {
            moveToState(Lifecycle.State.DESTROYED)
        }

        verify(viewModel).unregisterNavigationRequestListener(fragment)
    }

    @Test
    fun onCreate_ShouldRegisterNavigationListener() {
        launch()

        verify(viewModel).registerNavigationRequestListener(fragment)
    }
}