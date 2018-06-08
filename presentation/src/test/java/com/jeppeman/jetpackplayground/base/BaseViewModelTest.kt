package com.jeppeman.jetpackplayground.base

import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.domain.executor.PostExecutionThread
import com.jeppeman.jetpackplayground.navigation.NavigationRequest
import com.jeppeman.jetpackplayground.navigation.NavigationRequestListener
import com.jeppeman.jetpackplayground.ui.base.BaseViewModel
import com.jeppeman.jetpackplayground.util.getProperty
import com.jeppeman.jetpackplayground.util.invokeMethod
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

abstract class BaseViewModelTest<TViewModel : BaseViewModel> {

    private lateinit var spyViewModel: TViewModel
    protected abstract val viewModel: TViewModel
    protected val testScheduler = TestScheduler()

    abstract fun before()

    protected fun TViewModel.onInitialize() = invokeMethod("onInitialize")
    protected fun TViewModel.onCreate() = invokeMethod("onCreate")
    protected fun TViewModel.onStart() = invokeMethod("onStart")
    protected fun TViewModel.onResume() = invokeMethod("onResume")
    protected fun TViewModel.onPause() = invokeMethod("onPause")
    protected fun TViewModel.onStop() = invokeMethod("onStop")
    protected fun TViewModel.onDestroy() = invokeMethod("onDestroy")
    protected fun TViewModel.onCleared() = invokeMethod("onCleared")
    protected val TViewModel.disposables get() = getProperty<CompositeDisposable>("disposables")

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        before()
        viewModel.postExecutionThread = object : PostExecutionThread {
            override fun getScheduler() = testScheduler
        }
        spyViewModel = spy(viewModel)
    }

    @Test
    fun firstOnCreate_ShouldTriggerOnInitialize() {
        spyViewModel.onCreate()

        verify(spyViewModel).onInitialize()
    }

    @Test
    fun secondOnCreate_ShouldNotTriggerOnInitialize() {
        spyViewModel.onCreate()
        spyViewModel.onCreate()

        verify(spyViewModel, times(1)).onInitialize()
    }

    @Test
    fun requestNavigation_ShouldNotifyListeners() {
        val mockListener = mock(NavigationRequestListener::class.java)
        val mockRequest = mock(NavigationRequest::class.java)
        spyViewModel.registerNavigationRequestListener(mockListener)

        spyViewModel.requestNavigation(mockRequest)

        verify(mockListener).onNavigationRequest(mockRequest)
    }

    @Test
    fun onCleared_ShouldClearDisposables() {
        val disposables = spyViewModel.disposables
        disposables.add(Observable.just(0).subscribe())
        assertThat(1L).isEqualTo(disposables.size())

        spyViewModel.onCleared()

        assertThat(0L).isEqualTo(disposables.size())
    }
}