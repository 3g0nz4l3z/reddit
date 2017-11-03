package com.exequiel.redditor.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Date;

/**
 * Created by egonzalez on 03/11/17.
 */

public class DateUtil {
    private static final DateUtil ourInstance = new DateUtil();
    private static final String TAG = "DateUtil";

    public static DateUtil getInstance() {
        return ourInstance;
    }

    private DateUtil() {
    }

    public String getTimeElapsed(Context context, long timeStamp)
    {
        try {
            long now = new Date().getTime();
            Log.d(TAG, "getTimeElapsed " + timeStamp);
            return DateUtils.getRelativeDateTimeString(context, timeStamp, DateUtils.WEEK_IN_MILLIS, DateUtils.YEAR_IN_MILLIS, 0).toString();
        }catch (Exception e){

        }
        return "Several years ago";
    }

}
