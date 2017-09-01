package com.exequiel.redditor.reddit;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by m4ch1n3 on 19/8/2017.
 */

public class RedditPersister {
    private static SharedPreferences pref;

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
            String subredditId =  child.getString(RedditContract.SubReddits.SUBREDDIT_ID);
            String subredditName = child.getString(RedditContract.SubReddits.DISPLAY_NAME);
            cv.put(RedditContract.SubReddits.SUBREDDIT_ID, subredditId);
            cv.put(RedditContract.SubReddits.SUBREDDIT_ORDER, order);
            cv.put(RedditContract.SubReddits.DISPLAY_NAME, subredditName);
            cv.put(RedditContract.SubReddits.DISPLAY_NAME_PREFIXED, child.getString(RedditContract.SubReddits.DISPLAY_NAME_PREFIXED));
            cv.put(RedditContract.SubReddits.TITLE, child.getString(RedditContract.SubReddits.TITLE));
            cv.put(RedditContract.SubReddits.ICON_IMG, child.getString(RedditContract.SubReddits.ICON_IMG));
            cv.put(RedditContract.SubReddits.OVER18, child.getString(RedditContract.SubReddits.OVER18));
            contentValues.add(cv);

            if (i == 0){
                pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString(RedditContract.SubReddits.SUBREDDIT_ID,subredditId);
                edit.putString(RedditContract.SubReddits.DISPLAY_NAME, subredditName);
                edit.commit();

            }
        }
        if (contentValues.size() > 0) {
            ContentValues[] contentValuesFixedArray = new ContentValues[contentValues.size()];
            contentValues.toArray(contentValuesFixedArray);
            context.getContentResolver().delete(RedditContract.SubReddits.CONTENT_URI, null, null);
            context.getContentResolver().bulkInsert(RedditContract.SubReddits.CONTENT_URI, contentValuesFixedArray);
        }

    }

    public static void persistLinks(Context context, String order, JSONObject response, IProgresBarRefresher progresBarRefresher) throws JSONException {
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
            cv.put(RedditContract.Links.LINK_NUM_COMMENTS, child.getString(RedditContract.Links.LINK_NUM_COMMENTS));
//            cv.put(RedditContract.Links.LINK_OVER18, child.getString(RedditContract.Links.LINK_OVER18));
            contentValues.add(cv);

        }
        if (contentValues.size() > 0) {
            ContentValues[] contentValuesFixedArray = new ContentValues[contentValues.size()];
            contentValues.toArray(contentValuesFixedArray);
            //context.getContentResolver().delete(RedditContract.Links.CONTENT_URI,null, null);
            context.getContentResolver().delete(RedditContract.Links.CONTENT_URI, RedditContract.Links.LINK_ORDER+" = \""+order+"\"", null);

            context.getContentResolver().bulkInsert(RedditContract.Links.CONTENT_URI, contentValuesFixedArray);

            progresBarRefresher.refresh();
        }
    }

    public static void persistComments(Context context, String linkId, JSONArray response, IProgresBarRefresher progresBarRefresher) throws JSONException {
        //Make a recursive method to retrieve the comments :)
    }
}
