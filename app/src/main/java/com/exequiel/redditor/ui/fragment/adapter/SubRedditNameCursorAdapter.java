package com.exequiel.redditor.ui.fragment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.exequiel.redditor.R;
import com.exequiel.redditor.data.SubRedditLoader;
import com.exequiel.redditor.ui.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        ViewHolder viewHolder;
        View view = LayoutInflater.from(context).inflate(R.layout.subreddit_name_item, viewGroup, false);

        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String display_name_prefixed = cursor.getString(SubRedditLoader.Query.DISPLAY_NAME_PREFIXED);
        viewHolder.subredditName.setText(display_name_prefixed);
        String icon_image = cursor.getString(SubRedditLoader.Query.ICON_IMG);
        if (!icon_image.isEmpty()) {
            Log.d(TAG, icon_image);
            Glide.with(context).load(icon_image).into(viewHolder.iconImage);
        }
    }

    public static class ViewHolder {
        @BindView(R.id.textViewSubRedditName)
        TextView subredditName;
        @BindView(R.id.imageViewImageIcon)
        RoundedImageView iconImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
