package com.exequiel.redditor.reddit;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by m4ch1n3 on 19/8/2017.
 */

public class RedditPersister {

    private static final String TAG = RedditPersister.class.getCanonicalName();

    private RedditPersister() {

    }

    public static void persistSubReddits(Context context, String order, JSONObject response) throws JSONException {
        Log.d(TAG, "persistSubReddits");
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        JSONArray children = response.getJSONObject("data").getJSONArray("children");
        for (int i = 0; i < children.length(); i++) {
            JSONObject child = children.getJSONObject(i).getJSONObject("data");


            ContentValues cv = new ContentValues();
            cv.put(RedditContract.SubReddits.SUBREDDIT_ID, child.getString(RedditContract.SubReddits.SUBREDDIT_ID));
            cv.put(RedditContract.SubReddits.SUBREDDIT_ORDER, order);
            cv.put(RedditContract.SubReddits.DISPLAY_NAME, child.getString(RedditContract.SubReddits.DISPLAY_NAME));
            cv.put(RedditContract.SubReddits.DISPLAY_NAME_PREFIXED, child.getString(RedditContract.SubReddits.DISPLAY_NAME_PREFIXED));
            cv.put(RedditContract.SubReddits.TITLE, child.getString(RedditContract.SubReddits.TITLE));
            cv.put(RedditContract.SubReddits.ICON_IMG, child.getString(RedditContract.SubReddits.ICON_IMG));
            cv.put(RedditContract.SubReddits.OVER18, child.getString(RedditContract.SubReddits.OVER18));
            contentValues.add(cv);

        }
        if (contentValues.size() > 0) {
            ContentValues[] contentValuesFixedArray = new ContentValues[contentValues.size()];
            contentValues.toArray(contentValuesFixedArray);
            context.getContentResolver().delete(RedditContract.SubReddits.CONTENT_URI, null, null);
            context.getContentResolver().bulkInsert(RedditContract.SubReddits.CONTENT_URI, contentValuesFixedArray);
        }
    }

    public static void persistLinks(Context context, String order, JSONObject response) throws JSONException {
        Log.d(TAG, "persistLinks");
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        JSONArray children = response.getJSONObject("data").getJSONArray("children");
        for (int i = 0; i < children.length(); i++) {
            JSONObject child = children.getJSONObject(i).getJSONObject("data");


            ContentValues cv = new ContentValues();
            cv.put(RedditContract.Links.LINK_ORDER, order);
            cv.put(RedditContract.Links.LINK_ID, child.getString(RedditContract.Links.LINK_ID));
            cv.put(RedditContract.Links.LINK_DOMAIN, child.getString(RedditContract.Links.LINK_DOMAIN));
            cv.put(RedditContract.Links.LINK_AUTHOR, child.getString(RedditContract.Links.LINK_AUTHOR));
            cv.put(RedditContract.Links.LINK_SUBREDDIT, child.getString(RedditContract.Links.LINK_SUBREDDIT));
            cv.put(RedditContract.Links.LINK_SUBREDDIT_NAME_PREFIXED, child.getString(RedditContract.Links.LINK_SUBREDDIT_NAME_PREFIXED));
            cv.put(RedditContract.Links.LINK_TITLE, child.getString(RedditContract.Links.LINK_TITLE));
            cv.put(RedditContract.Links.LINK_SCORE, child.getString(RedditContract.Links.LINK_SCORE));
            cv.put(RedditContract.Links.LINK_SUBREDDIT_ID, child.getString(RedditContract.Links.LINK_SUBREDDIT_ID));
            cv.put(RedditContract.Links.LINK_THUMBNAIL, child.getString(RedditContract.Links.LINK_THUMBNAIL));
            cv.put(RedditContract.Links.LINK_PERMALINK, child.getString(RedditContract.Links.LINK_PERMALINK));
            cv.put(RedditContract.Links.LINK_URL, child.getString(RedditContract.Links.LINK_URL));
            cv.put(RedditContract.Links.LINK_CREATED, child.getString(RedditContract.Links.LINK_CREATED));
            cv.put(RedditContract.Links.LINK_IS_VIDEO, child.getString(RedditContract.Links.LINK_IS_VIDEO));
            contentValues.add(cv);

        }
        if (contentValues.size() > 0) {
            ContentValues[] contentValuesFixedArray = new ContentValues[contentValues.size()];
            contentValues.toArray(contentValuesFixedArray);
            //context.getContentResolver().delete(RedditContract.Links.CONTENT_URI,null, null);
            String[] args = new String[]{order};
            context.getContentResolver().delete(RedditContract.Links.CONTENT_URI, RedditContract.Links.LINK_ORDER+" = ?", args);

            context.getContentResolver().bulkInsert(RedditContract.Links.CONTENT_URI, contentValuesFixedArray);
        }
    }
}
