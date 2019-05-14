package com.zimplifica.redipuntos.libs;

import io.reactivex.annotations.NonNull;

public class FragmentViewModel<ViewType> {
    protected final Environment context;

    public FragmentViewModel(final @NonNull Environment environment){this.context = environment; }
}
