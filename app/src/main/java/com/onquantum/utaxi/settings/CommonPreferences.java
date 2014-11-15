package com.onquantum.utaxi.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.onquantum.utaxi.R;

import org.apache.http.impl.conn.SingleClientConnManager;

/**
 * Created by Admin on 10/14/14.
 */
public final class CommonPreferences {


    private SharedPreferences settings;

    public CommonPreferences(Context context) {
        settings = (SharedPreferences)context.getSharedPreferences(context.getResources().getString(R.string.app_name),Context.MODE_PRIVATE);
    }
}
