package com.onquantum.utaxi.wizard;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onquantum.utaxi.R;
import com.onquantum.utaxi.services.TrackingService;

import java.util.TimerTask;

/**
 * Created by Admin on 9/24/14.
 */
public class FragmentWizardStepOne extends AbstractFragmentWizard {
    private View root;
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private BroadcastReceiver locationBroadcastReceiver;
    private LatLng currentLocation;
    private boolean isCurrentLocationSet = false;
    private Address pickupAddress;
    private boolean isMapTouches = false;
    private TouchableWrapper touchableWrapper;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        currentWizardStep = 1;
        root = layoutInflater.inflate(R.layout.wizard_s_one, null);
        touchableWrapper = new TouchableWrapper(getActivity());
        touchableWrapper.addView(root);
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.current_address));

        if (googleMap == null) {
            mapFragment = (MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.map);
            googleMap = mapFragment.getMap();

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Log.i("info"," Camera Position = " + cameraPosition.toString());
                }
            });
        }

        ((Button) root.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
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

        return touchableWrapper;
    }

    @Override
    public View getView() {
        return root;
    }

    @Override
    public void onStart() {
        getActivity().startService(new Intent(getActivity(), TrackingService.class));
        locationBroadcastReceiver = new PositionReceiver();
        IntentFilter intentFilter = new IntentFilter(TrackingService.BROADCAST_LOCATION_CHANGE_ACTION);
        getActivity().registerReceiver( locationBroadcastReceiver,intentFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().stopService(new Intent(getActivity(),TrackingService.class));
        getActivity().unregisterReceiver(locationBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (map != null){
            getFragmentManager().beginTransaction().remove(map).commit();
            googleMap = null;
        }
    }

    private void updateCurrentPosition(LatLng latLng) {
        if (googleMap != null) {
            currentLocation = latLng;
            if (!isCurrentLocationSet) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                isCurrentLocationSet = true;
            }
        }
    }


    /* Inner classes */
    private class PositionReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            LatLng latLng = new LatLng(intent.getFloatExtra("latitude",0),intent.getFloatExtra("longitude",0));
            updateCurrentPosition(latLng);
        }
    }

    private class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }
        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("info"," Map ACTION_DOWN");
                    isMapTouches = true;
                    break;

                case MotionEvent.ACTION_UP:
                    Log.i("info"," Map ACTION_UP");
                    isMapTouches = false;
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
