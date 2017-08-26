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

import org.w3c.dom.Text;

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

        View view = LayoutInflater.from(context).inflate(R.layout.subrredit_link_post_list_item, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewSubrredit = (TextView) view.findViewById(R.id.textViewSubrredit);
        TextView textViewLinkTitle = (TextView) view.findViewById(R.id.textViewLinkTitle);
        TextView textViewLinkPoints = (TextView) view.findViewById(R.id.textViewLinkPoints);
        TextView textViewLinkComments = (TextView) view.findViewById(R.id.textViewLinkComments);
        TextView textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        ImageView imageViewIconPreview = (ImageView) view.findViewById(R.id.imageViewIconPreview);
        String stextViewSubrredit = cursor.getString(LinksLoader.Query.LINK_SUBREDDIT_NAME_PREFIXED);
        String stextViewLinkTitle = cursor.getString(LinksLoader.Query.LINK_TITLE);
        String stextViewLinkPoints = cursor.getString(LinksLoader.Query.LINK_SCORE);
        String stextViewLinkComments = cursor.getString(LinksLoader.Query.LINK_NUM_COMMENTS);
        String stextViewTime = cursor.getString(LinksLoader.Query.LINK_CREATED);
        String simageViewIconPreview = cursor.getString(LinksLoader.Query.LINK_THUMBNAIL);
        textViewLinkTitle.setText(stextViewLinkTitle);
        textViewLinkPoints.setText(stextViewLinkPoints);
        textViewSubrredit.setText(stextViewSubrredit);
        textViewTime.setText(stextViewTime);
        textViewLinkComments.setText(stextViewLinkComments);
        if (!simageViewIconPreview.isEmpty()){
            Log.d(TAG, simageViewIconPreview);
            Picasso.with(context).load(simageViewIconPreview).into(imageViewIconPreview);
        }
    }
}
