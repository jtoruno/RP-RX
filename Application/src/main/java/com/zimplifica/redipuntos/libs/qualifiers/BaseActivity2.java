package com.zimplifica.redipuntos.libs.qualifiers;

/*
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.ActivityLifecycleProvider;
import com.zimplifica.redipuntos.libs.ActivityViewModel;
import com.zimplifica.redipuntos.libs.ActivityViewModelManager;
import com.zimplifica.redipuntos.libs.utils.BundleUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;


public abstract class BaseActivity2<ViewModelType extends ActivityViewModel> extends AppCompatActivity implements ActivityLifecycleProvider {
    protected ViewModelType viewModel;
    private static final String VIEW_MODEL_KEY = "viewModel";
    private final PublishSubject<Void> back = PublishSubject.create();
    private final BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
    private final CompositeSubscription subscriptions = new CompositeSubscription();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignViewModel(savedInstanceState);

        this.lifecycle.onNext(ActivityEvent.CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            if (this.viewModel != null) {
                ActivityViewModelManager.getInstance().destroy(this.viewModel);
                this.viewModel = null;
            }
        }
    }

    protected final void startActivityWithTransition(final @NonNull Intent intent, final @AnimRes int enterAnim,
                                                     final @AnimRes int exitAnim) {
        startActivity(intent);
        overridePendingTransition(enterAnim, exitAnim);
    }

    private void assignViewModel(final @Nullable Bundle viewModelEnvelope) {
        if (this.viewModel == null) {
            final RequiresActivityViewModel annotation = getClass().getAnnotation(RequiresActivityViewModel.class);
            final Class<ViewModelType> viewModelClass = annotation == null ? null : (Class<ViewModelType>) annotation.value();
            if (viewModelClass != null) {
                this.viewModel = ActivityViewModelManager.getInstance().fetch(this,
                        viewModelClass,
                        BundleUtils.INSTANCE.maybeGetBundle(viewModelEnvelope, VIEW_MODEL_KEY));
            }
        }
    }
}
*/