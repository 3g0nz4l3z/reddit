package com.exequiel.redditor.ui.fragment;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.data.SubRedditLoader;
import com.exequiel.redditor.ui.fragment.adapter.SubRedditNameCursorAdapter;
import com.exequiel.redditor.ui.widget.SubredditAppWidgetProvider;

/**
 * Created by egonzalez on 8/23/17.
 */

public class SubRedditsNameListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    View rootView;
    private static final String TAG = SubRedditsNameListFragment.class.getCanonicalName();
    SubRedditNameCursorAdapter subRedditNameCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_subreddits_name_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        Cursor c = getActivity().getContentResolver().query(RedditContract.SubReddits.CONTENT_URI, SubRedditLoader.Query.PROJECTION, null, null, null);
        subRedditNameCursorAdapter = new SubRedditNameCursorAdapter(context, c);
        setListAdapter(subRedditNameCursorAdapter);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.i(TAG, "Item clicked: " + id);
        String subRedditName = "trending";
        String order = "hot";
        String displayNamePrefixed = "r/popular";
        Cursor c = getActivity().getContentResolver().query(RedditContract.SubReddits.CONTENT_URI, SubRedditLoader.Query.PROJECTION, "_id =" + id, null, null);
        if (c.moveToFirst()) {
            do {
                subRedditName = c.getString(SubRedditLoader.Query.DISPLAY_NAME);
                displayNamePrefixed = c.getString(SubRedditLoader.Query.DISPLAY_NAME_PREFIXED);

            } while (c.moveToNext());
        }
        Log.d(TAG, "onListItemClick: " + subRedditName);
        Bundle bundle = new Bundle();
        bundle.putString(RedditContract.SubReddits.DISPLAY_NAME, subRedditName);
        bundle.putString(RedditContract.SubReddits.SUBREDDIT_ORDER, order);
        SubRedditPostListFragment subRedditPostListFragment = new SubRedditPostListFragment();
        subRedditPostListFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.MainActivityFrameLayaout, subRedditPostListFragment).commit();

        /**
         * Also refresh the widget
         */
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return SubRedditLoader.allSubReddits(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        subRedditNameCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        subRedditNameCursorAdapter.swapCursor(null);
    }

    public void onDestroy() {
        super.onDestroy();

    }
}
