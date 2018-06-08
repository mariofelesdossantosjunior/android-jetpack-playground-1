package com.jeppeman.jetpackplayground.domain.interactor.base

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

abstract class ObservableUseCase<T, in Params> : BaseUseCase() {

    protected abstract fun buildUseCaseObservable(params: Params?): Observable<T>

    fun publish(params: Params? = null): Observable<T> {
        return buildUseCaseObservable(params)
                .subscribeOn(Schedulers.from(threadExecutor))
    }
}