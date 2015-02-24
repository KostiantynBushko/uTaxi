package com.onquantum.utaxi.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Admin on 11/28/14.
 */
public class GetStreetsService extends Service {

    public static String GET_STREET_SERVICE_INTENT_NAME = "com.onquantum.utaxi.service.GetStreetService";


    @Override
    public void onCreate() {
        Log.i("info","GetStreetService onCreate");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("info","GetStreetService onBind");
        return new Binder();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i("info","GetStreetService onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("info","GetStreetService onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("info", "GetStreetsService onStartCommand : flags = " + flags + " startId = " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("info","GetStreetService onDestroy");
        super.onDestroy();
    }
}
