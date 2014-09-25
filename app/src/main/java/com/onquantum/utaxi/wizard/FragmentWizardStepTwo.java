package com.onquantum.utaxi.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onquantum.utaxi.AbstractFragment;
import com.onquantum.utaxi.R;

/**
 * Created by Admin on 9/24/14.
 */
public class FragmentWizardStepTwo extends AbstractFragmentWizard {
    private View root;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        AbstractFragmentWizard.currentWizardStep = 2;
        root = layoutInflater.inflate(R.layout.wizard_step_two,null);
        return root;
    }
}
