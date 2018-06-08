package com.jeppeman.jetpackplayground.domain.interactor.base

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

abstract class FlowableUseCase<T, in Params> : BaseUseCase() {

    protected abstract fun buildUseCaseFlowable(params: Params?): Flowable<T>

    fun publish(params: Params? = null): Flowable<T> {
        return buildUseCaseFlowable(params)
                .subscribeOn(Schedulers.from(threadExecutor))
    }
}