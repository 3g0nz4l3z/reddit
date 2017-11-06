package com.exequiel.redditor.ui.fragment;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.data.SearchLoader;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.ui.fragment.adapter.SubredditSearchCursorAdapter;

/**
 * Created by m4ch1n3 on 2/10/2017.
 */

public class SubRedditSearchListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, IProgresBarRefresher {
    View rootView;
    private static final String TAG = SubRedditSearchListFragment.class.getCanonicalName();
    SubredditSearchCursorAdapter subredditSearchCursorAdapter;
    private LinearLayout listViewSearch;
    private LinearLayout progressBarContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subreddits_search_list, container, false);
        listViewSearch = (LinearLayout) rootView.findViewById(R.id.ListViewSearch);
        progressBarContainer = (LinearLayout) listViewSearch.findViewById(R.id.progressBarContainer);
        TextView textViewTitle = (TextView) getActivity().findViewById(R.id.textViewLinksTitle);
        textViewTitle.setText(getString(R.string.search_title));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        Cursor c = getActivity().getContentResolver().query(RedditContract.Search.CONTENT_URI, SearchLoader.Query.PROJECTION, null, null, null);
        subredditSearchCursorAdapter = new SubredditSearchCursorAdapter(context, c);
        setListAdapter(subredditSearchCursorAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return SearchLoader.allSearches(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        subredditSearchCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        subredditSearchCursorAdapter.swapCursor(null);

    }

    @Override
    public void refresh() {

        try {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "refresh()");
                    subredditSearchCursorAdapter.notifyDataSetChanged();
                    progressBarContainer.setVisibility(View.GONE);
                }
            });
        }catch(Exception e){

        }
    }

    @Override
    public void start_progress_bar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "start_progress_bar");
                progressBarContainer.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void end_progress_bar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "end_progress_bar");
                progressBarContainer.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        LinearLayout view = (LinearLayout) l.getChildAt(position);
        TextView subName = (TextView) view.findViewById(R.id.textViewSubRedditName);
        String subRedditName = subName.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString(RedditContract.SubReddits.DISPLAY_NAME, subRedditName);
        bundle.putString(RedditContract.SubReddits.SUBREDDIT_ORDER, "hot");
        SubRedditPostListFragment subRedditPostListFragment = new SubRedditPostListFragment();
        subRedditPostListFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.MainActivityFrameLayaout, subRedditPostListFragment).commit();

    }
}
