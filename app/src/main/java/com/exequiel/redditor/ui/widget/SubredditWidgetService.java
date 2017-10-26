package com.exequiel.redditor.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;
/**
 * Created by m4ch1n3 on 25/10/2017.
 */

public class SubredditWidgetService extends RemoteViewsService {
    private static final String TAG = "SubredditWidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory");
        return new SubredditListProvider(this.getApplicationContext(), intent);
    }
}
