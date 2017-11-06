package com.exequiel.redditor.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.exequiel.redditor.util.JobUtil;

import java.util.Random;


/**
 * Created by egonzalez on 31/10/17.
 */

public class OnCloseAppService extends Service {

    private static final String TAG = "OnCloseAppService";
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {

        public OnCloseAppService getService() {
            // Return this instance of LocalService so clients can call public methods
            return OnCloseAppService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JobUtil.scheduleJobRefreshSession(getApplicationContext());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG, "onTaskRemoved");
        getApplication().getSharedPreferences("AppPref", MODE_PRIVATE).edit().clear().commit();
    }
}
