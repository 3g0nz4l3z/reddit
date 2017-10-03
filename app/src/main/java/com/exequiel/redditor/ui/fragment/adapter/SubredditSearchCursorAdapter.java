package com.exequiel.redditor.ui.fragment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.SearchLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by m4ch1n3 on 2/10/2017.
 */

public class SubredditSearchCursorAdapter extends CursorAdapter {

    private String TAG = SubredditSearchCursorAdapter.class.getCanonicalName();

    public SubredditSearchCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(context).inflate(R.layout.subreddit_name_search_item, parent, false);

        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String display_name_prefixed = cursor.getString(SearchLoader.Query.SEARCH_DISPLAY_NAME);
        String description = cursor.getString(SearchLoader.Query.SEARCH_PUBLIC_DESCRIPTION);
        viewHolder.subredditName.setText(display_name_prefixed);
        viewHolder.textViewDescription.setText(description);
    }

    public static class ViewHolder {
        @BindView(R.id.textViewSubRedditName)
        TextView subredditName;
        @BindView(R.id.textViewDescription)
        TextView textViewDescription;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
