package com.exequiel.redditor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.data.SubRedditLoader;
import com.exequiel.redditor.reddit.RedditRestClient;


/**
 * Fetch de data after the app authenticated with reddit
 */

public class AuthClientReceiver extends BroadcastReceiver {

    private static final String TAG = AuthClientReceiver.class.getCanonicalName();
    public static final String ACTION = "com.exequiel.redditor.AUTHCLIENTRECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        new RedditRestClient(context).retrieveSubreddits("popular");
        String [] orders = {"hot", "new", "controversial", "top"};
        String [] columns = new String[]{RedditContract.SubReddits.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(RedditContract.SubReddits.CONTENT_URI, SubRedditLoader.Query.PROJECTION, null, null, null);
        if(cursor.moveToFirst()){
           do {
               Log.d(TAG, "AuthClientReceiver");
                String subbreditName =cursor.getString(SubRedditLoader.Query.DISPLAY_NAME);
               for (String order: orders) {
                   Log.d(TAG, order+" - "+subbreditName);
                   new RedditRestClient(context).retrieveLinks(subbreditName, order);
               }
           }while(cursor.moveToNext());
       }
       cursor.close();


    }
}
