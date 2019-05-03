package com.zimplifica.redipuntos.libs;

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

public class ActivityViewModel2<ViewType extends ActivityLifecycleType>{
    private final PublishSubject<ViewType> viewChange = PublishSubject.create();
    private final Observable<ViewType> view = this.viewChange.filter(v -> v != null);
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final PublishSubject<ActivityResult> activityResult = PublishSubject.create();

    private final PublishSubject<Intent> intent = PublishSubject.create();

    protected final Environment context;

    public ActivityViewModel2(final @NonNull Environment context){
        this.context = context;
    }

    public void activityResult(final @NonNull ActivityResult activityResult) {
        this.activityResult.onNext(activityResult);
    }

    /**
     * Takes intent data from the view.
     */
    public void intent(final @NonNull Intent intent) {
        this.intent.onNext(intent);
    }

    @CallSuper
    protected void onCreate(final @NonNull Context context, final @Nullable Bundle savedInstanceState) {
        dropView();
    }

    @CallSuper
    protected void onResume(final @NonNull ViewType view) {
        onTakeView(view);
    }

    @CallSuper
    protected void onPause() {
        dropView();
    }

    @CallSuper
    protected void onDestroy() {

        this.subscriptions.clear();
        this.viewChange.onComplete();
    }

    private void onTakeView(final @NonNull ViewType view) {
        this.viewChange.onNext(view);
    }

    private void dropView() {
        this.viewChange.onNext(null);
    }
    protected @NonNull Observable<ActivityResult> activityResult() {
        return this.activityResult;
    }

    protected @NonNull Observable<Intent> intent() {
        return this.intent;
    }

    public @NonNull <T> ObservableTransformer<T, T> bindToLifecycle() {
        return source -> source.takeUntil(
                this.view.switchMap(v -> v.lifecycle().map(e -> Pair.create(v, e)))
                        .filter(ve -> isFinished(ve.first, ve.second))
        );
    }

    /**
     * Determines from a view and lifecycle event if the view's life is over.
     */
    private boolean isFinished(final @NonNull ViewType view, final @NonNull ActivityEvent event) {

        if (view instanceof BaseActivity) {
            return event == ActivityEvent.DESTROY && ((BaseActivity) view).isFinishing();
        }

        return event == ActivityEvent.DESTROY;
    }
}
