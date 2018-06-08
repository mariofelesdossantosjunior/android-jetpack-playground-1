package com.jeppeman.jetpackplayground.domain.interactor.base

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

abstract class SingleUseCase<T, in Params> : BaseUseCase() {
    protected abstract fun buildUseCaseSingle(params: Params?): Single<T>

    fun publish(params: Params? = null): Single<T> {
        return buildUseCaseSingle(params)
                .subscribeOn(Schedulers.from(threadExecutor))
    }
}