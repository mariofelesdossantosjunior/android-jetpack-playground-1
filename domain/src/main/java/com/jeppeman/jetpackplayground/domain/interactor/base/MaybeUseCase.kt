package com.jeppeman.jetpackplayground.domain.interactor.base

import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

abstract class MaybeUseCase<T, in Params> : BaseUseCase() {
    protected abstract fun buildUseCaseMaybe(params: Params?): Maybe<T>

    fun publish(params: Params? = null): Maybe<T> {
        return buildUseCaseMaybe(params)
                .subscribeOn(Schedulers.from(threadExecutor))
    }
}