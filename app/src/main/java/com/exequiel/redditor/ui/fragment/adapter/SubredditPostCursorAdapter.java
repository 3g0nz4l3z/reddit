package com.exequiel.redditor.ui.fragment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.LinksLoader;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by egonzalez on 8/23/17.
 */

public class SubredditPostCursorAdapter extends CursorAdapter {

    private String TAG = SubredditPostCursorAdapter.class.getCanonicalName();

    public SubredditPostCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        View view = LayoutInflater.from(context).inflate(R.layout.subrredit_link_post_list_item, viewGroup, false);


        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String stextViewSubrredit = cursor.getString(LinksLoader.Query.LINK_SUBREDDIT_NAME_PREFIXED);
        String stextViewLinkTitle = cursor.getString(LinksLoader.Query.LINK_TITLE);
        String stextViewLinkPoints = cursor.getString(LinksLoader.Query.LINK_SCORE);
        String stextViewLinkComments = cursor.getString(LinksLoader.Query.LINK_NUM_COMMENTS);
        String stextViewTime = cursor.getString(LinksLoader.Query.LINK_CREATED);
        String simageViewIconPreview = cursor.getString(LinksLoader.Query.LINK_THUMBNAIL);
        viewHolder.textViewLinkTitle.setText(stextViewLinkTitle);
        viewHolder.textViewLinkPoints.setText(stextViewLinkPoints);
        viewHolder.textViewSubrredit.setText(stextViewSubrredit);
        viewHolder.textViewTime.setText(stextViewTime);
        viewHolder.textViewLinkComments.setText(stextViewLinkComments);
        try {

            if (!simageViewIconPreview.isEmpty() || !simageViewIconPreview.equals("self")) {
                Log.d(TAG, simageViewIconPreview);
                Picasso.with(context).load(simageViewIconPreview).into(viewHolder.imageViewIconPreview);
            }
        }catch(Exception e){

        }
    }


    public static class ViewHolder {
        @BindView(R.id.textViewSubrredit)
        TextView textViewSubrredit;
        @BindView(R.id.textViewLinkTitle)
        TextView textViewLinkTitle;
        @BindView(R.id.textViewLinkPoints)
        TextView textViewLinkPoints;
        @BindView(R.id.textViewLinkComments)
        TextView textViewLinkComments;
        @BindView(R.id.textViewTime)
        TextView textViewTime;
        @BindView(R.id.imageViewIconPreview)
        ImageView imageViewIconPreview;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
