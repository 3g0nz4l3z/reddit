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
                RedditContract.Links._ID,
        };
        int _ID = 0;
    }
}
