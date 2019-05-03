package com.zimplifica.redipuntos.extensions

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.annotations.NonNull
import io.reactivex.rxkotlin.withLatestFrom

fun <E, S> Observable<S>.takeWhen(other: Observable<E>): Observable<Pair<E,S>>{
    return other.withLatestFrom<E, S>(this@takeWhen).map { tuple -> tuple }
}
