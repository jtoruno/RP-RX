package com.zimplifica.redipuntos.libs;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.trello.rxlifecycle.ActivityEvent;
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity;
import com.zimplifica.redipuntos.ui.data.ActivityResult;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class ActivityViewModel<ViewType> {

    /*
    private final PublishSubject<ViewType> viewChange = PublishSubject.create();
    private final Observable<ViewType> view = this.viewChange.filter(v -> v != null);
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final PublishSubject<Instrumentation.ActivityResult> activityResult = PublishSubject.create();

    private final PublishSubject<Intent> intent = PublishSubject.create();

    */

    protected final Environment context;

    public ActivityViewModel(final @NonNull Environment context){
        this.context = context;
    }

}

