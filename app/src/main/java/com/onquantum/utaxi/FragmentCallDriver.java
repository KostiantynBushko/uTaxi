package com.onquantum.utaxi;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.onquantum.utaxi.wizard.FragmentWizardStepOne;

/**
 * Created by Admin on 9/22/14.
 */
public class FragmentCallDriver extends AbstractFragment{
    private View root;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceSatte) {
        root = layoutInflater.inflate(R.layout.fragment_order,null);
        ((RelativeLayout)root.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info",getResources().getString(R.string.kyivstar));
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:0982345322"));
                startActivity(intent);
            }
        });

        ((RelativeLayout)root.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info",getResources().getString(R.string.mts));
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:0509654484"));
                startActivity(intent);
            }
        });

        ((RelativeLayout)root.findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info",getResources().getString(R.string.life));
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:0937254622"));
                startActivity(intent);
            }
        });

        ((RelativeLayout)root.findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),OnlineOrder.class);
                getActivity().startActivity(intent);
            }
        });

        ((RelativeLayout)root.findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.move_in_up, R.anim.move_out_up,R.anim.move_in_down, R.anim.move_out_down);
                fragmentTransaction.replace(R.id.fragment,new FragmentWizardStepOne());
                fragmentTransaction.addToBackStack(FragmentWizardStepOne.class.getName());
                fragmentTransaction.commit();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("info","FragmentCallDriver Resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("info","FragmentCallDriver Pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("info","FragmentCallDriver Stop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("info","FragmentCallDriver DestroyView");
        if (fragmentsCommonInterface != null) {
            fragmentsCommonInterface.onCloseFragment();
        } else if (getActivity() instanceof FragmentsCommonInterface) {
            ((MainActivity)getActivity()).onCloseFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("info","FragmentCallDriver Destroy");
    }
}
