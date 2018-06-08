package com.jeppeman.jetpackplayground.domain.interactor.base


import com.jeppeman.jetpackplayground.domain.executor.ThreadExecutor
import javax.inject.Inject

abstract class BaseUseCase {
    @Inject
    internal lateinit var threadExecutor: ThreadExecutor
}