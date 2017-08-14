package com.exequiel.redditor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by m4ch1n3 on 13/8/2017.
 */

public class RedditDBHelper extends SQLiteOpenHelper {
    private static final String name = "name";
    private static final int version = 1;

    public RedditDBHelper(Context context) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
