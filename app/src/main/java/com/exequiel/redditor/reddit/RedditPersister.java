package com.exequiel.redditor.reddit;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
            String subredditId = child.getString(RedditContract.SubReddits.SUBREDDIT_ID);
            String subredditName = child.getString(RedditContract.SubReddits.DISPLAY_NAME);
            cv.put(RedditContract.SubReddits.SUBREDDIT_ID, subredditId);
            cv.put(RedditContract.SubReddits.SUBREDDIT_ORDER, order);
            cv.put(RedditContract.SubReddits.DISPLAY_NAME, subredditName);
            cv.put(RedditContract.SubReddits.DISPLAY_NAME_PREFIXED, child.getString(RedditContract.SubReddits.DISPLAY_NAME_PREFIXED));
            cv.put(RedditContract.SubReddits.TITLE, child.getString(RedditContract.SubReddits.TITLE));
            cv.put(RedditContract.SubReddits.ICON_IMG, child.getString(RedditContract.SubReddits.ICON_IMG));
            cv.put(RedditContract.SubReddits.OVER18, child.getString(RedditContract.SubReddits.OVER18));
            contentValues.add(cv);

            if (i == 0) {
                pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString(RedditContract.SubReddits.SUBREDDIT_ID, subredditId);
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
            try {
                JSONArray jAImages = child.getJSONObject("preview").getJSONArray("images");
                JSONObject jOImage = jAImages.getJSONObject(0).getJSONObject("source");
                String image = jOImage.getString("url");
                Log.d(TAG, "image "+image);
                cv.put(RedditContract.Links.LINK_IMAGE, image);

            }catch (Exception e){

            }
//            cv.put(RedditContract.Links.LINK_OVER18, child.getString(RedditContract.Links.LINK_OVER18));
            contentValues.add(cv);

        }
        if (contentValues.size() > 0) {
            ContentValues[] contentValuesFixedArray = new ContentValues[contentValues.size()];
            contentValues.toArray(contentValuesFixedArray);
            //context.getContentResolver().delete(RedditContract.Links.CONTENT_URI,null, null);
            context.getContentResolver().delete(RedditContract.Links.CONTENT_URI, RedditContract.Links.LINK_ORDER + " = \"" + order + "\"", null);

            context.getContentResolver().bulkInsert(RedditContract.Links.CONTENT_URI, contentValuesFixedArray);

            progresBarRefresher.refresh();
        }
    }

    /**
     *
     * @param context
     * @param linkId
     * @param response
     * @param progresBarRefresher
     * @throws JSONException
     */
    public static void persistComments(Context context, String linkId, JSONArray response, IProgresBarRefresher progresBarRefresher) throws JSONException {
        //Make a recursive method to retrieve the comments :)
        JSONObject comments = response.getJSONObject(1);
        JSONArray children = comments.getJSONObject("data").getJSONArray("children");
        context.getContentResolver().delete(RedditContract.Comments.CONTENT_URI, null, null);
        try {
            persistCommentsInternal(context, children, "");
        }catch (Exception e){

        }
        progresBarRefresher.refresh();
    }

    /**
     *
     * @param context
     * @param children
     * @param parentId
     * @throws JSONException
     */
    private static void persistCommentsInternal(Context context, JSONArray children, String parentId) throws JSONException {
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();

            for (int i = 0; i < children.length(); i++) {
                JSONObject child = children.getJSONObject(i).getJSONObject("data");
                ContentValues cv = new ContentValues();
                cv.put(RedditContract.Comments.COMMENTS_PARENT_ID, parentId);
                cv.put(RedditContract.Comments.COMMENTS_ID,  child.getString(RedditContract.Comments.COMMENTS_ID));
                cv.put(RedditContract.Comments.COMMENTS_LINK_ID,  child.getString(RedditContract.Comments.COMMENTS_LINK_ID));
                cv.put(RedditContract.Comments.COMMENTS_AUTHOR,  child.getString(RedditContract.Comments.COMMENTS_AUTHOR));
                cv.put(RedditContract.Comments.COMMENTS_SUBREDDIT_ID,  child.getString(RedditContract.Comments.COMMENTS_SUBREDDIT_ID));
                cv.put(RedditContract.Comments.COMMENTS_SCORE,  child.getString(RedditContract.Comments.COMMENTS_SCORE));
                cv.put(RedditContract.Comments.COMMENTS_BODY, child.getString( RedditContract.Comments.COMMENTS_BODY));
                cv.put(RedditContract.Comments.COMMENTS_CREATED,  child.getString(RedditContract.Comments.COMMENTS_CREATED));
                contentValues.add(cv);
                if (child.has("replies")){
                    Log.d(TAG, "replies");
                    try {
                        JSONObject replies = child.getJSONObject("replies");
                        JSONArray childrenReplies  = replies.getJSONObject("data").getJSONArray("children");
                        persistCommentsInternal(context, childrenReplies, child.getString(RedditContract.Comments.COMMENTS_ID));
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d(TAG, "persistCommentsInternal por persistir");
            if (contentValues.size() > 0) {
                Log.d(TAG, "persistCommentsInternal a persistir");
                ContentValues[] contentValuesFixedArray = new ContentValues[contentValues.size()];
                contentValues.toArray(contentValuesFixedArray);
                //context.getContentResolver().delete(RedditContract.Links.CONTENT_URI,null, null);

                context.getContentResolver().bulkInsert(RedditContract.Comments.CONTENT_URI, contentValuesFixedArray);
            }
            Log.d(TAG, "persistCommentsInternal end");

        return;
    }

    public static void persistSearch(Context context, JSONObject response, IProgresBarRefresher iProgresBarRefresher) throws JSONException {
        Log.d(TAG, "persistSearch");
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        JSONArray children = response.getJSONObject("data").getJSONArray("children");
        for (int i = 0; i < children.length(); i++) {
            JSONObject child = children.getJSONObject(i).getJSONObject("data");


            ContentValues cv = new ContentValues();
                cv.put(RedditContract.Search.SEARCH_DISPLAY_NAME, child.getString(RedditContract.Search.SEARCH_DISPLAY_NAME));
                cv.put(RedditContract.Search.SEARCH_PUBLIC_DESCRIPTION, child.getString(RedditContract.Search.SEARCH_PUBLIC_DESCRIPTION));
                contentValues.add(cv);

        }
        if (contentValues.size() > 0) {
            ContentValues[] contentValuesFixedArray = new ContentValues[contentValues.size()];
            contentValues.toArray(contentValuesFixedArray);
            context.getContentResolver().delete(RedditContract.Search.CONTENT_URI, null, null);
            context.getContentResolver().bulkInsert(RedditContract.Search.CONTENT_URI, contentValuesFixedArray);
        }
        iProgresBarRefresher.refresh();
    }
}
