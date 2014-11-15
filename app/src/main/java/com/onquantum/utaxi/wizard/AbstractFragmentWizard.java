package com.onquantum.utaxi.wizard;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onquantum.utaxi.AbstractFragment;

/**
 * Created by Admin on 9/24/14.
 */
public class AbstractFragmentWizard extends Fragment {

    public interface WizardCommonInterface {
        public void OnStartExecuteWizard();
        public void OnSwitchStep(int step);
        public void OnCloseWizard();
    }

    WizardCommonInterface wizardCommonInterface;

    public static int currentWizardStep = 0;

    @Override
    public void onAttach(Activity activity) {
        Log.i("info","AbstractFragmentWizard onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("info","AbstractFragmentWizard onCreate");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i("info","AbstractFragmentWizard  onStart");
    }

    @Override
    public void onResume() {
        Log.i("info","AbstractFragmentWizard onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("info","AbstractFragmentWizard onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("info", "AbstractFragmentWizard  onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("info","AbstractFragmentWizard onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("info","AbstractFragmentWizard onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("info","AbstractFragmentWizard onDetach");
        super.onDetach();
    }

    public void onRecognizer(String result){}

}
