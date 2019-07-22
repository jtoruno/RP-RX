package com.zimplifica.redipuntos.libs;

import android.content.Intent;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;

public class ActivityViewModel<ViewType> {

    /*
    private final PublishSubject<ViewType> viewChange = PublishSubject.create();
    private final Observable<ViewType> view = this.viewChange.filter(v -> v != null);
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final PublishSubject<Instrumentation.ActivityResult> activityResult = PublishSubject.create();

    private final PublishSubject<Intent> intent = PublishSubject.create();

    */
    private final PublishSubject<Intent> intent = PublishSubject.create();

    protected final Environment context;

    public ActivityViewModel(final @NonNull Environment context){
        this.context = context;
    }

    /**
     * Takes intent data from the view.
     */
    public void intent(final @NonNull Intent intent) {
        this.intent.onNext(intent);
    }

    protected @NonNull Observable<Intent> intent() {
        return this.intent;
    }
}

