package com.exequiel.redditor.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

/**
 * Created by m4ch1n3 on 19/8/2017.
 */

public class LinksLoader extends CursorLoader {

    private static final String TAG = LinksLoader.class.getCanonicalName();

    private LinksLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    private LinksLoader(Context context, String selection) {
        super(context, RedditContract.Links.CONTENT_URI, Query.PROJECTION, selection, null, null);

    }

    public static LinksLoader allLinks(Context context) {
        return new LinksLoader(context, RedditContract.Links.buildDirUri());
    }

    public static LinksLoader fromSubRedditId(Context context, long linkId) {
        return new LinksLoader(context, RedditContract.Links.buildUriWithRowId(linkId));
    }

    /**
     * I have to fix this
     *
     * @param context
     * @param order
     * @param subRedditId
     * @return
     */
    public static LinksLoader allLinksByOrderBySubRedditId(Context context, String order, String subRedditId) {
        Log.d(TAG, subRedditId);
        String selection = RedditContract.Links.LINK_ORDER + " = " + order + " AND " + RedditContract.Links.LINK_SUBREDDIT_ID + " =" + subRedditId;
        return new LinksLoader(context, selection);
    }

    public static LinksLoader allLinksByOrderBySubRedditName(Context context, String order, String subRedditName) {
        Log.d(TAG, subRedditName);
        String selection = RedditContract.Links.LINK_ORDER + " = " + order + " AND " + RedditContract.Links.LINK_SUBREDDIT + " =" + subRedditName;
        return new LinksLoader(context, selection);
    }

    public interface Query {
        String[] PROJECTION = {
                RedditContract.Links._ID,
                RedditContract.Links.LINK_ORDER,
                RedditContract.Links.LINK_ID,
                RedditContract.Links.LINK_DOMAIN,
                RedditContract.Links.LINK_AUTHOR,
                RedditContract.Links.LINK_SUBREDDIT,
                RedditContract.Links.LINK_SUBREDDIT_NAME_PREFIXED,
                RedditContract.Links.LINK_TITLE,
                RedditContract.Links.LINK_SCORE,
                RedditContract.Links.LINK_SUBREDDIT_ID,
                RedditContract.Links.LINK_THUMBNAIL,
                RedditContract.Links.LINK_PERMALINK,
                RedditContract.Links.LINK_URL,
                RedditContract.Links.LINK_CREATED,
                RedditContract.Links.LINK_IS_VIDEO,
                RedditContract.Links.LINK_NUM_COMMENTS,
//                RedditContract.Links.LINK_OVER18,
        };
        int _ID = 0;
        int LINK_ORDER = 1;
        int LINK_ID = 2;
        int LINK_DOMAIN = 3;
        int LINK_AUTHOR = 4;
        int LINK_SUBREDDIT = 5;
        int LINK_SUBREDDIT_NAME_PREFIXED = 6;
        int LINK_TITLE = 7;
        int LINK_SCORE = 8;
        int LINK_SUBREDDIT_ID = 9;
        int LINK_THUMBNAIL = 10;
        int LINK_PERMALINK = 11;
        int LINK_URL = 12;
        int LINK_CREATED = 13;
        int LINK_IS_VIDEO = 14;
        int LINK_NUM_COMMENTS = 15;
//        int LINK_OVER18 = 15;
    }
}
