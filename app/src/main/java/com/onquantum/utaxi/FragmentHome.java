package com.onquantum.utaxi;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Admin on 9/22/14.
 */
public class FragmentHome extends AbstractFragment {

    private View root;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        root = layoutInflater.inflate(R.layout.fragment_home, null);

        ((Button)root.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.move_in_up, R.anim.move_out_up,R.anim.move_in_down, R.anim.move_out_down);
                fragmentTransaction.replace(R.id.fragment,new FragmentCallDriver());
                fragmentTransaction.addToBackStack(FragmentCallDriver.class.getName());
                fragmentTransaction.commit();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("info", "FragmentHome Resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("info","FragmentHome Pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("info","FragmentHome Stop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("info","FragmentHome DestroyView");
        if (fragmentsCommonInterface != null) {
            fragmentsCommonInterface.onCloseFragment();
        } else if (getActivity() instanceof FragmentsCommonInterface) {
            ((MainActivity)getActivity()).onCloseFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("info","FragmentHome Destroy");
    }
}
