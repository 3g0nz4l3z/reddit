package com.exequiel.redditor.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.exequiel.redditor.R;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.ui.activity.PostActivity;

/**
 * Created by m4ch1n3 on 25/10/2017.
 */

class SubredditListProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "SubredditListProvider";
    private Context context;
    private int appWidgetId;
    private Cursor cursor;


    public SubredditListProvider(Context applicationContext, Intent intent) {
        this.context = applicationContext;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged");
        try {
            cursor.close();
        } catch (Exception e) {

        }

        cursor = context.getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, null, null, null);
    }

    @Override
    public void onDestroy() {
        try {
            cursor.close();
        }catch (Exception e){

        }
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.subrredit_widget_link_post_list_item);

        if (cursor.moveToPosition(position)) {
            String subRedditId = cursor.getString(LinksLoader.Query.LINK_SUBREDDIT_NAME_PREFIXED);
            String stextViewSubrredit = cursor.getString(LinksLoader.Query.LINK_SUBREDDIT_NAME_PREFIXED);
            String stextViewLinkTitle = cursor.getString(LinksLoader.Query.LINK_TITLE);
            String stextViewLinkPoints = cursor.getString(LinksLoader.Query.LINK_SCORE);
            String stextViewLinkComments = cursor.getString(LinksLoader.Query.LINK_NUM_COMMENTS);
            String stextViewTime = cursor.getString(LinksLoader.Query.LINK_CREATED);
            String simageViewIconPreview = cursor.getString(LinksLoader.Query.LINK_THUMBNAIL);

            remoteView.setTextViewText(R.id.textViewSubrredit, stextViewSubrredit);
            remoteView.setTextViewText(R.id.textViewLinkTitle, stextViewLinkTitle);
            remoteView.setTextViewText(R.id.textViewLinkPoints, stextViewLinkPoints);
            remoteView.setTextViewText(R.id.textViewLinkComments, stextViewLinkComments);
            remoteView.setTextViewText(R.id.textViewTime, stextViewTime);

            try {

                if (!simageViewIconPreview.isEmpty() || !simageViewIconPreview.equals("self")) {
                    AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.imageViewIconPreview, remoteView, appWidgetId);
                    Glide.with(context).asBitmap().load(simageViewIconPreview).into(appWidgetTarget);
                }
            } catch (Exception e) {

            }
            /*
            Launch the post activity
             */
            Intent intent = new Intent(context, PostActivity.class);
            intent.putExtra(RedditContract.Links._ID, cursor.getString(LinksLoader.Query._ID));
            intent.putExtra(RedditContract.Links.LINK_ID, cursor.getString(LinksLoader.Query.LINK_ID));
            intent.putExtra(RedditContract.Links.LINK_SUBREDDIT,  cursor.getString(LinksLoader.Query.LINK_SUBREDDIT));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteView.setOnClickPendingIntent(R.id.widget, pendingIntent);
            }

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
