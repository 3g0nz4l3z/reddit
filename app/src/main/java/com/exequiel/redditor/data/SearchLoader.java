package com.exequiel.redditor.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by m4ch1n3 on 27/8/2017.
 */

public class SearchLoader extends CursorLoader{

    private SearchLoader(Context context, Uri uri) {
        super(context, uri, SearchLoader.Query.PROJECTION, null, null, null);
    }

    public static SearchLoader allSearches(Context context) {
        return new SearchLoader(context, RedditContract.Search.buildDirUri());
    }

    public static SearchLoader searchFromId(Context context, long seachId) {
        return new SearchLoader(context, RedditContract.Search.buildUriWithRowId(seachId));
    }

    public interface Query {
        String[] PROJECTION = {
                RedditContract.Search._ID,
                RedditContract.Search.SEARCH_DISPLAY_NAME,
                RedditContract.Search.SEARCH_PUBLIC_DESCRIPTION,
        };
        int _ID = 0;
        int SEARCH_DISPLAY_NAME = 1;
        int SEARCH_PUBLIC_DESCRIPTION = 2;
    }
}
