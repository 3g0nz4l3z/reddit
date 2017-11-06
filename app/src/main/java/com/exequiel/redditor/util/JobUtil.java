package com.exequiel.redditor.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.exequiel.redditor.service.RefreshSessionJobService;

/**
 * Created by egonzalez on 06/11/17.
 */

public class JobUtil {

    public final static long FORTY_FIVE_MINUTES = 2700000;

    private static final JobUtil ourInstance = new JobUtil();

    public static JobUtil getInstance() {
        return ourInstance;
    }

    private JobUtil() {
    }


    public static void scheduleJob(Context context, Class jobClass, long interval) {
        ComponentName serviceComponent = new ComponentName(context, jobClass);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setTriggerContentUpdateDelay(interval);
        builder.setPeriodic(interval);
        builder.setPersisted(true);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void scheduleJobRefreshSession(Context context){
        scheduleJob(context, RefreshSessionJobService.class, FORTY_FIVE_MINUTES);

    }
}
