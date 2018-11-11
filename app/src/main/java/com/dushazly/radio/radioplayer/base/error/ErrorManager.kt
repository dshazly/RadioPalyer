package com.dushazly.radio.radioplayer.error

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function


object ErrorManager {

    fun <T> wrap(observable: Observable<T>): Observable<T> {
        return observable.onErrorResumeNext(ExceptionsInterceptor())
    }

    /**
     * Maps java exceptions to the approriate [AppException]
     */
    private class ExceptionsInterceptor<T> : Function<Throwable, Observable<T>> {


        @Throws(Exception::class)
        override fun apply(throwable: Throwable): Observable<T> {
            return Observable.error(AppException.adapt(throwable))
        }
    }

    private class ExceptionsInterceptorSingle<T> : Function<Throwable, Single<T>> {


        @Throws(Exception::class)
        override fun apply(throwable: Throwable): Single<T> {
            return Single.error(AppException.adapt(throwable))
        }
    }
}/* No instances */
