package com.exequiel.redditor.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.exequiel.redditor.reddit.RedditRestClient;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONException;

/**
 * Created by egonzalez on 06/11/17.
 */

public class RefreshSessionJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {

        Context context = getApplicationContext();
        RedditRestClient redditRestClient = new RedditRestClient(context);
        boolean bLogin = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE).getBoolean("logIn", false);
        if (!bLogin){
            try {
                redditRestClient.getTokenFoInstalledClient();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {
                redditRestClient.getTokenForAuthCode();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
