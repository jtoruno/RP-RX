package com.zimplifica.redipuntos.libs;

import android.content.Context;
import android.support.annotation.CallSuper;
import io.reactivex.annotations.NonNull;

public class ActivityViewModel<ViewType> {
    protected final Context context;

    public ActivityViewModel(final @NonNull Context context){
        this.context = context;
    }

}
