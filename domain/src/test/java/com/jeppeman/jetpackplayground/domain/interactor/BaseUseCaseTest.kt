package com.jeppeman.jetpackplayground.domain.interactor

import com.jeppeman.jetpackplayground.domain.executor.ThreadExecutor
import com.jeppeman.jetpackplayground.domain.interactor.base.BaseUseCase
import io.reactivex.schedulers.TestScheduler
import org.junit.Before

abstract class BaseUseCaseTest<TUseCase : BaseUseCase> {
    protected lateinit var testScheduler: TestScheduler
    abstract val useCase: TUseCase

    abstract fun init()

    @Before
    fun setUp() {
        init()
        testScheduler = TestScheduler()
        useCase.threadExecutor = object : ThreadExecutor {
            override fun execute(runnable: Runnable?) {
                testScheduler.scheduleDirect(runnable!!)
            }
        }
    }
}