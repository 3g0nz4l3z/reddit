package com.exequiel.redditor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Fetch de data afeter the user authenticated with reddit
 */

public class AuthUserReceiver extends BroadcastReceiver {

    private static final String TAG = AuthUserReceiver.class.getCanonicalName();
    public static final String ACTION = "com.exequiel.redditor.AUTHSERRECEIVER";
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
