package com.exequiel.redditor.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.exequiel.redditor.R;

/**
 * Created by m4ch1n3 on 25/10/2017.
 */

public class SubredditAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "SubredditWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.subreddit_widget);

            Intent swsIntent = new Intent(context, SubredditWidgetService.class);
            swsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            swsIntent.setData(Uri.parse(swsIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(appWidgetId, R.id.subredditWidgetListView, swsIntent);


            appWidgetManager.updateAppWidget(appWidgetId, views);
            super.onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive");
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.subreddit_widget);
            Intent swsIntent = new Intent(context, SubredditWidgetService.class);
            swsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            swsIntent.setData(Uri.parse(swsIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(appWidgetId, R.id.subredditWidgetListView, swsIntent);

            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            int appWidgetIds[] = appWidgetManager
                    .getAppWidgetIds(new ComponentName(context, SubredditAppWidgetProvider.class));

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
                    R.id.subredditWidgetListView);

        }
    }
}


