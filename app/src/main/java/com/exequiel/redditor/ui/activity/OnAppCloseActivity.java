package com.exequiel.redditor.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.exequiel.redditor.service.OnCloseAppService;

/**
 * Created by egonzalez on 31/10/17.
 */

public class OnAppCloseActivity extends AppCompatActivity {

    private static final String TAG = "OnAppCloseActivity";
    OnCloseAppService mService;


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, OnCloseAppService.class);
        startService(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

}
