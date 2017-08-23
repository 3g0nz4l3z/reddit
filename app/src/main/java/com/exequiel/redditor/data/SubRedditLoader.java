package com.exequiel.redditor.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class SubRedditLoader extends CursorLoader {

    public static SubRedditLoader allSubReddits(Context context){
        return new SubRedditLoader(context, RedditContract.SubReddits.buildDirUri());
    }

    public static SubRedditLoader fromSubRedditId(Context context, long subRedditId){
        return new SubRedditLoader(context, RedditContract.SubReddits.buildUriWithRowId(subRedditId));
    }


    private SubRedditLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public interface Query {
        String[] PROJECTION = {
                RedditContract.SubReddits._ID,
                RedditContract.SubReddits.SUBREDDIT_ORDER,
                RedditContract.SubReddits.SUBREDDIT_ID,
                RedditContract.SubReddits.DISPLAY_NAME,
                RedditContract.SubReddits.DISPLAY_NAME_PREFIXED,
                RedditContract.SubReddits.ICON_IMG,
                RedditContract.SubReddits.OVER18,
                RedditContract.SubReddits.TITLE,
        };

        int _ID = 0;
        int SUBREDDIT_ORDER = 1;
        int SUBREDDIT_ID = 2;
        int DISPLAY_NAME = 3;
        int DISPLAY_NAME_PREFIXED = 4;
        int ICON_IMG = 5;
        int OVER18 = 6;
        int TITLE = 7;
    }
}
