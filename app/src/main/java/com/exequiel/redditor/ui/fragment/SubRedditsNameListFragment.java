package com.exequiel.redditor.ui.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.data.SubRedditLoader;
import com.exequiel.redditor.ui.fragment.adapter.SubRedditNameCursorAdapter;

/**
 * Created by egonzalez on 8/23/17.
 */

public class SubRedditsNameListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    SubRedditNameCursorAdapter subRedditNameCursorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_subreddits_name_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        Cursor c = context.getContentResolver().query(RedditContract.SubReddits.CONTENT_URI, SubRedditLoader.Query.PROJECTION, null, null, null);
        subRedditNameCursorAdapter = new SubRedditNameCursorAdapter(context, c);
        setListAdapter(subRedditNameCursorAdapter);
        getLoaderManager().initLoader(0, null, this);

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
}
