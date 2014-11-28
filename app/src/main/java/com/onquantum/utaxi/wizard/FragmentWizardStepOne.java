package com.onquantum.utaxi.wizard;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.onquantum.utaxi.R;
import com.onquantum.utaxi.common.SpeechRecognitionHelper;
import com.onquantum.utaxi.services.TrackingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Admin on 9/24/14.
 */
public class FragmentWizardStepOne extends AbstractFragmentWizard {

    private View root;
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private BroadcastReceiver locationBroadcastReceiver;
    private ServiceConnection trackingServiceConnection;
    private boolean trackingServiceIsBind = false;
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
    private boolean clickNextButton = false;
    private boolean isMapTouchesMove = false;

    private Intent tsIntent;
    private Timer timer;

    // UI Elements
    private ProgressBar progressBar = null;
    private Bundle savedInstanceState = null;

    private Button nextButton = null;

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
            root = layoutInflater.inflate(R.layout.wizard_step_one, container,false);
            touchableWrapper = new TouchableWrapper(getActivity());
            touchableWrapper.addView(root);
            tvAddress = (TextView)root.findViewById(R.id.textView);
            progressBar = (ProgressBar)root.findViewById(R.id.progressBar);

            if (googleMap == null) {
                googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        cameraLocation = cameraPosition.target;
                    }
                });
            }
        }catch (InflateException e){}

        nextButton = (Button)root.findViewById(R.id.button);
        nextButton.setOnClickListener(new View.OnClickListener() {
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
        //nextButton.setEnabled(false);
        if (cameraLocation != null) {
            updateCurrentPosition(cameraLocation);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }



        ((ImageButton)root.findViewById(R.id.voiceButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechRecognitionHelper.run(getActivity());
            }
        });

        if (currentLocation == null) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().startService(new Intent(getActivity(), TrackingService.class));
                    trackingServiceConnection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            Log.i("info","Bind");
                            trackingServiceIsBind = true;
                        }
                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                            Log.i("info","Unbind");
                            trackingServiceIsBind = false;
                        }
                    };
                    //tsIntent = new Intent(getActivity(),TrackingService.class);
                    //getActivity().bindService(tsIntent, trackingServiceConnection, Activity.BIND_AUTO_CREATE);
                    locationBroadcastReceiver = new PositionReceiver();
                    IntentFilter intentFilter = new IntentFilter(TrackingService.BROADCAST_LOCATION_CHANGE_ACTION);
                    getActivity().registerReceiver( locationBroadcastReceiver,intentFilter);
                    isTouchEnable = true;
                }
            },800);
        }

        Locale locale = new Locale(language,country);
        geocoder = new Geocoder(getActivity(),locale);

        return touchableWrapper;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (currentLocation != null) {
            this.savedInstanceState = null;
            this.savedInstanceState = new Bundle();
            this.savedInstanceState.putDouble("lat",currentLocation.latitude);
            this.savedInstanceState.putDouble("lng",currentLocation.longitude);
            this.savedInstanceState.putString("address",tvAddress.getText().toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
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
            try {
                getFragmentManager().beginTransaction().remove(map).commit();
            }catch (RuntimeException e) {}
            googleMap = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        //getActivity().unbindService(trackingServiceConnection);
        super.onDestroy();
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
        geocoder(result);
    }

    private void updateCurrentPosition(LatLng latLng) {
        if (googleMap != null) {
            currentLocation = latLng;
            if (!isCurrentLocationSet) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                isCurrentLocationSet = true;
                geocode(currentLocation);
            }
        }
    }

    private void geocode(LatLng location) {
        try {
            if (location != null){
                List<Address>addressList =  geocoder.getFromLocation(location.latitude,location.longitude,1);
                if (!addressList.isEmpty()) {
                    Log.i("info", " Address : " + addressList.get(0).toString());
                    pickupAddress = addressList.get(0);
                    String thoroughfare = pickupAddress.getThoroughfare();
                    String feature = pickupAddress.getFeatureName();
                    progressBar.setVisibility(View.GONE);
                    if (thoroughfare != null) {
                        if (thoroughfare.equals(feature)) {
                            tvAddress.setText(pickupAddress.getThoroughfare());
                        } else {
                            tvAddress.setText(pickupAddress.getThoroughfare() + " " + pickupAddress.getFeatureName());
                        }
                    }else {
                        tvAddress.setText(pickupAddress.getThoroughfare() + " " + pickupAddress.getFeatureName());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void geocoder(String string) {
        Geocoder geoc = new Geocoder(getActivity());
        try {
            ArrayList<Address>addresses = (ArrayList<Address>) geoc
                    .getFromLocationName(string + "Львів" + "Львівська область" + "Україна",1);
            for (Address add : addresses) {
                double lat = add.getLatitude();
                double lon = add.getLongitude();
                LatLng latLng = new LatLng(lat,lon);
                pickupAddress = add;
                String thoroughfare = pickupAddress.getThoroughfare();
                String feature = pickupAddress.getFeatureName();
                if (thoroughfare.equals(feature)){
                    tvAddress.setText(pickupAddress.getThoroughfare());
                } else {
                    tvAddress.setText(pickupAddress.getThoroughfare() + " " + pickupAddress.getFeatureName());
                }
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            //nextButton.setEnabled(true);
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
        GeocodeTimerTask geocodeTimerTask = null;
        float x,y = 0;
        float d = 0;
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
                    x = event.getX();
                    y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("info","ACTION_UP");
                    d = (float) Math.sqrt(Math.pow(event.getX() - x,2) + Math.pow(event.getY() - y, 2));
                    isMapTouches = false;
                    if (cameraLocation != null && isMapTouchesMove == true) {
                        isMapTouchesMove = false;
                        new Timer().schedule(geocodeTimerTask = new GeocodeTimerTask(), 5000);
                    }
                    x = 0;
                    y = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("info"," ACTION_MOVE x = " + x + " y = " + y);
                    if (x > 0 && y > 0)
                        d = (float) Math.sqrt(Math.pow(event.getX() - x,2) + Math.pow(event.getY() - y, 2));
                    if (d > 30) {
                        isMapTouchesMove = true;
                        Log.i("info"," distance =  " + d);
                        if (geocodeTimerTask != null) {
                            geocodeTimerTask.cancel();
                            geocodeTimerTask = null;
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        tvAddress.setText(getActivity().getResources().getText(R.string.searching));
                    } else {
                        x += event.getX();
                        y += event.getY();
                    }
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }

    /* Geocode task*/
    class GeocodeTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("i","Run in UI thread : location = " + cameraLocation.latitude);
                    geocode(cameraLocation);
                }
            });
        }
    }
}
