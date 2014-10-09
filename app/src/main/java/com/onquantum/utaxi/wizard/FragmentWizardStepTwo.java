package com.onquantum.utaxi.wizard;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onquantum.utaxi.AbstractFragment;
import com.onquantum.utaxi.R;

/**
 * Created by Admin on 9/24/14.
 */
public class FragmentWizardStepTwo extends AbstractFragmentWizard {
    private View root;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        currentWizardStep = 3;
        root = layoutInflater.inflate(R.layout.wizard_step_two,null);
        ((Button)root.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.move_left_in,R.anim.move_left_out,
                        R.anim.move_right_in,R.anim.move_right_out
                );
                transaction.replace(R.id.fragment, new FragmentWizardStepThree());
                transaction.addToBackStack(FragmentWizardStepThree.class.getName());
                transaction.commit();

            }
        });
        return root;
    }
}
