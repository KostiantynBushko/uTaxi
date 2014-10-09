package com.onquantum.utaxi.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Admin on 9/29/14.
 */
public class TrackingService extends Service implements LocationListener{

    private static final long  MIN_UPDATE_TIME_MS = 1000;
    private static final float MIN_UPDATE_DISTANCE = 1;
    private LocationManager locationManager;

    public final static String BROADCAST_LOCATION_CHANGE_ACTION = "com.onquantum.utaxi.services.trackingservice.location";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("info"," TrackingService onCreate");
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("info","TrackingService onStartCommand : flags = " + flags + " startId = " + startId);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                this.onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_UPDATE_TIME_MS,MIN_UPDATE_DISTANCE,this);
        }else {
            Toast.makeText(getApplicationContext(),"GPS is not enable",Toast.LENGTH_LONG).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("info","TrackingService onDestroy");
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("info","TrackingService = " + location.toString());
        Intent intent = new Intent(BROADCAST_LOCATION_CHANGE_ACTION);
        intent.putExtra("latitude",(float)location.getLatitude());
        intent.putExtra("longitude",(float)location.getLongitude());
        sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}