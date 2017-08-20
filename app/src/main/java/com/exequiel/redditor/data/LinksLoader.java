package com.exequiel.redditor.data;

import android.content.Context;
import android.content.CursorLoader;

/**
 * Created by m4ch1n3 on 19/8/2017.
 */

public class LinksLoader extends CursorLoader {
    private LinksLoader(Context context) {
        super(context);
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
                RedditContract.Links.LINK_OVER18,
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
        int LINK_OVER18 = 15;
    }
}
