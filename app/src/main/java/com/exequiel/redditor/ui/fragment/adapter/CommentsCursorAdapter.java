package com.exequiel.redditor.ui.fragment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;

/**
 * Created by m4ch1n3 on 28/8/2017.
 */

public class CommentsCursorAdapter extends SimpleCursorTreeAdapter {
    public CommentsCursorAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor cursor) {
        return null;
    }
}
