package com.onquantum.utaxi;

import android.app.Fragment;
import android.util.Log;

/**
 * Created by Admin on 9/24/14.
 */
public class AbstractFragment extends Fragment {
    protected FragmentsCommonInterface fragmentsCommonInterface;

    public void setOnFragmentCommonInterface(FragmentsCommonInterface fragmentsCommonInterface) {
        this.fragmentsCommonInterface = fragmentsCommonInterface;
    }

    @Override
    public void onStart() {
        if (fragmentsCommonInterface != null) {
            fragmentsCommonInterface.onRunFragment();
        } else if (getActivity() instanceof FragmentsCommonInterface) {
            ((FragmentsCommonInterface) getActivity()).onRunFragment();
        }
        super.onStart();
    }
    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fragmentsCommonInterface != null) {
            fragmentsCommonInterface.onCloseFragment(this);
        } else if (getActivity() instanceof FragmentsCommonInterface) {
            ((MainActivity)getActivity()).onCloseFragment(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
