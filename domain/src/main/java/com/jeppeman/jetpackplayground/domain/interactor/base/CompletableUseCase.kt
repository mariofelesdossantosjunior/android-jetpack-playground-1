package com.jeppeman.jetpackplayground.domain.interactor.base

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

abstract class CompletableUseCase<in Params> : BaseUseCase() {

    protected abstract fun buildUseCaseCompletable(params: Params?): Completable

    fun publish(params: Params? = null): Completable {
        return buildUseCaseCompletable(params)
                .subscribeOn(Schedulers.from(threadExecutor))
    }
}