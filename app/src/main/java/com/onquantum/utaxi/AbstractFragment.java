package com.onquantum.utaxi;

import android.app.Fragment;

/**
 * Created by Admin on 9/24/14.
 */
public class AbstractFragment extends Fragment {
    protected FragmentsCommonInterface fragmentsCommonInterface;

    public void setOnFragmentCommonInterface(FragmentsCommonInterface fragmentsCommonInterface) {
        this.fragmentsCommonInterface = fragmentsCommonInterface;
    }
}
