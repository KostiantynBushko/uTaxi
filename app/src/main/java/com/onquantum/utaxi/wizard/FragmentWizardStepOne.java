package com.onquantum.utaxi.wizard;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onquantum.utaxi.R;
import com.onquantum.utaxi.common.Constant;
import com.onquantum.utaxi.common.SpeechRecognitionHelper;
import com.onquantum.utaxi.services.TrackingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Admin on 9/24/14.
 */
public class FragmentWizardStepOne extends AbstractFragmentWizard {

    private View root;
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private BroadcastReceiver locationBroadcastReceiver;
    private LatLng currentLocation;
    private LatLng cameraLocation;
    private boolean isCurrentLocationSet = false;
    private Address pickupAddress;
    private boolean isMapTouches = false;
    private TouchableWrapper touchableWrapper;
    private boolean isTouchEnable = false;
    private TextView tvAddress;
    private String country;
    private String language;
    private Geocoder geocoder;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        currentWizardStep = 1;
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.current_address));

        country = ((TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();
        language = Locale.getDefault().getLanguage();

        if (root != null) {
            ViewGroup parent = (ViewGroup)root.getParent();
            if (parent != null)
                parent.removeView(root);
        }
        try {
            root = layoutInflater.inflate(R.layout.wizard_s_one, container,false);
            touchableWrapper = new TouchableWrapper(getActivity());
            touchableWrapper.addView(root);
            if (googleMap == null) {
                googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        Log.i("info"," Camera Position = " + cameraPosition.target);
                        cameraLocation = cameraPosition.target;
                    }
                });
            }
        }catch (InflateException e){}


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
        tvAddress = (TextView)root.findViewById(R.id.textView);
        ((ImageButton)root.findViewById(R.id.voiceButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechRecognitionHelper.run(getActivity());
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().startService(new Intent(getActivity(), TrackingService.class));
                locationBroadcastReceiver = new PositionReceiver();
                IntentFilter intentFilter = new IntentFilter(TrackingService.BROADCAST_LOCATION_CHANGE_ACTION);
                getActivity().registerReceiver( locationBroadcastReceiver,intentFilter);
                isTouchEnable = true;
            }
        },1000);
        Locale locale = new Locale(language,country);
        Log.i("info"," Locale : Country = "
                + locale.getCountry()
                + " Language = " + locale.getLanguage()
                + " Locale = " + locale.getISO3Language());
        geocoder = new Geocoder(getActivity(),locale);
        return touchableWrapper;
    }

    @Override
    public View getView() {
        return root;
    }

    @Override
    public void onDestroyView() {
        getActivity().stopService(new Intent(getActivity(),TrackingService.class));
        try {
            getActivity().unregisterReceiver(locationBroadcastReceiver);
        }catch (IllegalArgumentException e) {}

        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (map != null){
            getFragmentManager().beginTransaction().remove(map).commit();
            googleMap = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        isCurrentLocationSet = false;
        super.onStop();
    }
    @Override
    public void onDetach() {
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.app_name));
        super.onDetach();
    }

    @Override
    public void onRecognizer(String result){
        Log.i("info"," On Recognizer = " + result);
    }

    private void updateCurrentPosition(LatLng latLng) {
        if (googleMap != null) {
            currentLocation = latLng;
            if (!isCurrentLocationSet) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                isCurrentLocationSet = true;
                geocoder(currentLocation);
            }
        }
    }

    private void geocoder(LatLng location) {
        Log.i("info"," start geicoding ");
        try {
            if (location != null){
                List<Address>addressList =  geocoder.getFromLocation(location.latitude,location.longitude,1);
                if (!addressList.isEmpty()) {
                    pickupAddress = addressList.get(0);
                    String thoroughfare = pickupAddress.getThoroughfare();
                    String feature = pickupAddress.getFeatureName();
                    Log.i("info"," Thoroughfare : " + thoroughfare + "  featureName : " + feature);
                    if (thoroughfare.equals(feature)){
                        Log.i("info"," **** thoroughfare equals feature ");
                        tvAddress.setText(pickupAddress.getThoroughfare());
                    } else {
                        tvAddress.setText(pickupAddress.getThoroughfare() + " " + pickupAddress.getFeatureName());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    if (cameraLocation != null)
                        geocoder(cameraLocation);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
