package com.zimplifica.redipuntos.libs;

import android.content.Context;
import android.support.annotation.CallSuper;
import io.reactivex.annotations.NonNull;

public class ActivityViewModel<ViewType> {
    protected final Environment context;

    public ActivityViewModel(final @NonNull Environment context){
        this.context = context;
    }

}
