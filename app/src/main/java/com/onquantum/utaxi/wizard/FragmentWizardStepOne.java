package com.onquantum.utaxi.wizard;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onquantum.utaxi.R;
import com.onquantum.utaxi.services.TrackingService;

/**
 * Created by Admin on 9/24/14.
 */
public class FragmentWizardStepOne extends AbstractFragmentWizard {
    private View root;
    private GoogleMap googleMap;
    private BroadcastReceiver locationBroadcastReceiver;
    private LatLng currentLocation;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        currentWizardStep = 1;
        root = layoutInflater.inflate(R.layout.wizard_s_one, null);

        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.current_address));

        if (googleMap == null) {
            googleMap = ((MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Log.i("info"," Camera Position = " + cameraPosition.toString());
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(cameraPosition.target));
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
        if (map != null)
            getFragmentManager().beginTransaction().remove(map).commit();
    }

    private class PositionReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            LatLng latLng = new LatLng(intent.getFloatExtra("latitude",0),intent.getFloatExtra("longitude",0));
            Log.i("info","PositionReceiver : lat = " + latLng.latitude + " log = " + latLng.longitude);
            updateCurrentPosition(latLng);
        }
    }

    private void updateCurrentPosition(LatLng latLng) {
        if (googleMap != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
