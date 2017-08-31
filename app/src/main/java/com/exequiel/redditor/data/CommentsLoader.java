package com.exequiel.redditor.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by m4ch1n3 on 27/8/2017.
 */

public class CommentsLoader extends CursorLoader{

    private CommentsLoader(Context context, Uri uri) {
        super(context, uri, LinksLoader.Query.PROJECTION, null, null, null);
    }

    public static CommentsLoader allComments(Context context) {
        return new CommentsLoader(context, RedditContract.Comments.buildDirUri());
    }

    public static CommentsLoader fromCommentId(Context context, long linkId) {
        return new CommentsLoader(context, RedditContract.Comments.buildUriWithRowId(linkId));
    }

    public interface Query {
        String[] PROJECTION = {
                RedditContract.Comments._ID,
                RedditContract.Comments.COMMENTS_ID,
                RedditContract.Comments.COMMENTS_LINK_ID,
                RedditContract.Comments.COMMENTS_AUTHOR,
                RedditContract.Comments.COMMENTS_SUBREDDIT_ID,
                RedditContract.Comments.COMMENTS_SCORE,
                RedditContract.Comments.COMMENTS_BODY,
                RedditContract.Comments.COMMENTS_CREATED,
        };
        int _ID = 0;
        int COMMENTS_ID = 1;
        int COMMENTS_LINK_ID = 2;
        int COMMENTS_AUTHOR = 3;
        int COMMENTS_SUBREDDIT_ID = 4;
        int COMMENTS_SCORE = 5;
        int COMMENTS_CREATED = 6;
    }
}
