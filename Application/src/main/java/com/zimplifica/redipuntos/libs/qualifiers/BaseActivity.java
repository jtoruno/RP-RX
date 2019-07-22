package com.zimplifica.redipuntos.libs.qualifiers;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.AnimRes;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.zimplifica.redipuntos.libs.ActivityViewModel;
import com.zimplifica.redipuntos.libs.ActivityViewModelManager;
import com.zimplifica.redipuntos.libs.utils.BundleUtils;

public abstract class BaseActivity<ViewModelType extends ActivityViewModel> extends AppCompatActivity {
    protected ViewModelType viewModel;
    private static final String VIEW_MODEL_KEY = "viewModel";

    public ViewModelType viewModel() {
        return this.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignViewModel(savedInstanceState);
        this.viewModel.intent(getIntent());
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

    /**
     * Called when an activity is set to `singleTop` and it is relaunched while at the top of the activity stack.
     */
    @CallSuper
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.viewModel.intent(intent);
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


