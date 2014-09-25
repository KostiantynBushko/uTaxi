package com.onquantum.utaxi.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onquantum.utaxi.R;

/**
 * Created by Admin on 9/25/14.
 */
public class FragmentWizardStepThree extends AbstractFragmentWizard {

    private View root;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup group, Bundle savedInstanceState) {
        root = layoutInflater.inflate(R.layout.wizard_step_three,null);
        return root;
    }
}
