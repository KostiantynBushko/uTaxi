package com.onquantum.utaxi.wizard;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.onquantum.utaxi.AbstractFragment;
import com.onquantum.utaxi.R;
import com.onquantum.utaxi.services.TrackingService;

/**
 * Created by Admin on 9/24/14.
 */
public class FragmentWizardStepTwo extends AbstractFragmentWizard {
    private View root;
    private GoogleMap googleMap;
    private boolean trackingServiceIsBind = false;
    private LatLng currentLocation;
    private LatLng cameraLocation;
    private boolean isMapTouches = false;
    private TouchableWrapper touchableWrapper;
    private boolean isTouchEnable = false;
    private TextView tvAddress;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        currentWizardStep = 3;
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.destination_address));

        if (root != null) {
            ViewGroup parent = (ViewGroup)root.getParent();
            if (parent != null)
                parent.removeView(root);
        }
        try {
            root = layoutInflater.inflate(R.layout.wizard_step_two, container,false);
            touchableWrapper = new TouchableWrapper(getActivity());
            touchableWrapper.addView(root);
            /*if (googleMap == null) {
                googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map_des)).getMap();
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        Log.i("info", " Camera Position = " + cameraPosition.target);
                        cameraLocation = cameraPosition.target;
                    }
                });
            }*/
        }catch (InflateException e){}

        ((Button) root.findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.move_left_in, R.anim.move_left_out,
                        R.anim.move_right_in, R.anim.move_right_out
                );
                transaction.replace(R.id.fragment, new FragmentWizardStepTwo());
                transaction.addToBackStack(FragmentWizardStepTwo.class.getName());
                transaction.commit();
            }
        });

        //tvAddress = (TextView)root.findViewById(R.id.textView);
        //tvAddress.setText(R.string.destination_address);

        isTouchEnable = true;
        return touchableWrapper;
    }

    @Override
    public View getView() {
        return root;
    }

    @Override
    public void onDestroyView() {
        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map_des);
        if (map != null){
            getFragmentManager().beginTransaction().remove(map).commit();
            googleMap = null;
        }
        super.onDestroyView();
    }

    private class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }
        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (!isTouchEnable)
                return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("info","ACTION_DOWN");
                    isMapTouches = true;
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("info","ACTION_UP");
                    isMapTouches = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
