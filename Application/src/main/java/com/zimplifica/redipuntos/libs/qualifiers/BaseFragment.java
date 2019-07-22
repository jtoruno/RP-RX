package com.zimplifica.redipuntos.libs.qualifiers;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.zimplifica.redipuntos.libs.FragmentViewModel;
import com.zimplifica.redipuntos.libs.FragmentViewModelManager;
import com.zimplifica.redipuntos.libs.utils.BundleUtils;

public class BaseFragment<ViewModelType extends FragmentViewModel> extends Fragment {
    private static final String VIEW_MODEL_KEY = "FragmentViewModel";
    protected ViewModelType viewModel;

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignViewModel(savedInstanceState);

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (getActivity().isFinishing()) {
            if (this.viewModel != null) {
                // Order of the next two lines is important: the lifecycle should update before we
                // complete the view publish subject in the view model.
                FragmentViewModelManager.getInstance().destroy(this.viewModel);
                this.viewModel = null;
            }
        }
    }


    private void assignViewModel(final @Nullable Bundle viewModelEnvelope) {
        if (this.viewModel == null) {
            final RequiresFragmentViewModel annotation = getClass().getAnnotation(RequiresFragmentViewModel.class);
            final Class<ViewModelType> viewModelClass = annotation == null ? null : (Class<ViewModelType>) annotation.value();
            if (viewModelClass != null) {
                this.viewModel = FragmentViewModelManager.getInstance().fetch(getContext(),
                        viewModelClass,
                        BundleUtils.INSTANCE.maybeGetBundle(viewModelEnvelope, VIEW_MODEL_KEY));
            }
        }
    }
}
