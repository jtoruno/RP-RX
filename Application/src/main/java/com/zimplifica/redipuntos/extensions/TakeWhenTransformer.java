package com.zimplifica.redipuntos.extensions;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;

public final class TakeWhenTransformer<S, T> implements ObservableTransformer<S, S> {
    @NonNull
    private final Observable<T> when;

    public TakeWhenTransformer(final @NonNull Observable<T> when) {
        this.when = when;
    }

    @Override
    public ObservableSource<S> apply(Observable<S> upstream) {
        return this.when.withLatestFrom(upstream,(__, x) -> x);
    }
}