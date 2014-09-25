package com.onquantum.utaxi.wizard;

import android.app.Fragment;

/**
 * Created by Admin on 9/24/14.
 */
public class AbstractFragmentWizard extends Fragment {

    public interface WizardCommonInterface {
        public void OnStartExecuteWizard();
        public int OnSwitchStep();
    }
    public static int currentWizardStep = 0;
}
