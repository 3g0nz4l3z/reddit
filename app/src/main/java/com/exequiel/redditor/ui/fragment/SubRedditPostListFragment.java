package com.exequiel.redditor.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.ui.fragment.adapter.SubredditPostCursorAdapter;

public class SubRedditPostListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static SharedPreferences pref;
    SubredditPostCursorAdapter subredditPostCursorAdapter;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String SUBREDDIT_ORDER = "subRedditOrder";

        public SubRedditPostListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SubRedditPostListFragment newInstance(String subRedditOrder) {
            SubRedditPostListFragment fragment = new SubRedditPostListFragment();
            Bundle args = new Bundle();
            args.putString(SUBREDDIT_ORDER, subRedditOrder);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_subreddit, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(SUBREDDIT_ORDER)));
            return rootView;
        }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String order = args.getString(SUBREDDIT_ORDER,"hot");
        pref = getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String subRedditId = args.getString(RedditContract.SubReddits.SUBREDDIT_ID, pref.getString(RedditContract.SubReddits.SUBREDDIT_ID,""));
        return LinksLoader.allLinksByOrderBySubReddit(getActivity(), order, subRedditId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        subredditPostCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        subredditPostCursorAdapter.swapCursor(null);

    }
}