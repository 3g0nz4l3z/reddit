package com.exequiel.redditor.ui.fragment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.SubRedditLoader;
import com.exequiel.redditor.ui.RoundedImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by egonzalez on 8/23/17.
 */

public class SubRedditNameCursorAdapter extends CursorAdapter {

    private String TAG = SubRedditNameCursorAdapter.class.getCanonicalName();

    public SubRedditNameCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.subreddit_name_item, viewGroup, false);
        //bindView(view, context, cursor);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //if (cursor.moveToFirst()){
        //    do {
                TextView subredditName = (TextView) view.findViewById(R.id.textViewSubRedditName);
                String display_name_prefixed = cursor.getString(SubRedditLoader.Query.DISPLAY_NAME_PREFIXED);
                subredditName.setText(display_name_prefixed);
                RoundedImageView iconImage = (RoundedImageView) view.findViewById(R.id.imageViewImageIcon);
                String icon_image = cursor.getString(SubRedditLoader.Query.ICON_IMG);
                if (!icon_image.isEmpty()){
                    Log.d(TAG, icon_image);
                    Picasso.with(context).load(icon_image).into(iconImage);
                }
    }
}
