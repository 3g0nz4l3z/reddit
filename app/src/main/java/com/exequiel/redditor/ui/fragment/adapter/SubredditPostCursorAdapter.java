package com.exequiel.redditor.ui.fragment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by egonzalez on 8/23/17.
 */

public class SubredditPostCursorAdapter extends CursorAdapter{

    private String TAG = SubredditPostCursorAdapter.class.getCanonicalName();

    public SubredditPostCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
