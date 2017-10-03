package com.exequiel.redditor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.exequiel.redditor.data.RedditProvider.Tables;

public class RedditDBHelper extends SQLiteOpenHelper {
    private static final String name = "RedditDB";
    private static final int version = 1;
    private static final String TAG = RedditDBHelper.class.getCanonicalName();


    public RedditDBHelper(Context context) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        final String CREATE_TABLE_SUBREDDITS = "CREATE TABLE " + Tables.SUBREDDITS + " ( "
                + RedditContract.SubRedditsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RedditContract.SubRedditsColumns.SUBREDDIT_ORDER + " TEXT NOT NULL, "
                + RedditContract.SubRedditsColumns.SUBREDDIT_ID + " TEXT NOT NULL, "
                + RedditContract.SubRedditsColumns.DISPLAY_NAME + " TEXT NOT NULL, "
                + RedditContract.SubRedditsColumns.DISPLAY_NAME_PREFIXED + " TEXT NOT NULL, "
                + RedditContract.SubRedditsColumns.ICON_IMG + " TEXT NOT NULL, "
                + RedditContract.SubRedditsColumns.OVER18 + " TEXT NOT NULL, "
                + RedditContract.SubRedditsColumns.TITLE + " TEXT NOT NULL"
                + ");";

        final String CREATE_TABLE_LINKS = "CREATE TABLE " + Tables.LINKS + " ( "
                + RedditContract.Links._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RedditContract.Links.LINK_ORDER + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_ID + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_DOMAIN + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_AUTHOR + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_SUBREDDIT + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_SUBREDDIT_NAME_PREFIXED + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_TITLE + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_SCORE + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_SUBREDDIT_ID + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_THUMBNAIL + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_PERMALINK + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_URL + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_CREATED + " TEXT NOT NULL, "
                + RedditContract.Links.LINK_IS_VIDEO + " TEXT NOT NULL,"
                + RedditContract.Links.LINK_NUM_COMMENTS + " TEXT NOT NULL,"
                + RedditContract.Links.LINK_IMAGE + " TEXT"
//                + RedditContract.Links.LINK_OVER18 + " TEXT NOT NULL"
                + ");";
        String CREATE_TABLE_COMMENTS = "CREATE TABLE " + Tables.COMMENTS + " ( "
                +RedditContract.Comments._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RedditContract.Comments.COMMENTS_ID + " TEXT NOT NULL, "
                + RedditContract.Comments.COMMENTS_PARENT_ID + " TEXT NOT NULL, "
                + RedditContract.Comments.COMMENTS_LINK_ID + " TEXT NOT NULL, "
                + RedditContract.Comments.COMMENTS_SUBREDDIT_ID + " TEXT NOT NULL, "
                + RedditContract.Comments.COMMENTS_AUTHOR + " TEXT NOT NULL, "
                + RedditContract.Comments.COMMENTS_BODY + " TEXT NOT NULL, "
                + RedditContract.Comments.COMMENTS_SCORE + " TEXT NOT NULL, "
                + RedditContract.Comments.COMMENTS_CREATED + " TEXT NOT NULL "
                + ");";
        String CREATE_TABLE_SEARCH = "CREATE TABLE " + Tables.SEARCH + " ( "
                +RedditContract.Search._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RedditContract.Search.SEARCH_DISPLAY_NAME + " TEXT NOT NULL, "
                + RedditContract.Search.SEARCH_PUBLIC_DESCRIPTION + " TEXT NOT NULL "
                + ");";



        db.execSQL(CREATE_TABLE_SUBREDDITS);
        db.execSQL(CREATE_TABLE_LINKS);
        db.execSQL(CREATE_TABLE_COMMENTS);
        db.execSQL(CREATE_TABLE_SEARCH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SUBREDDITS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.LINKS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SEARCH);
        onCreate(db);

    }
}
