package com.exequiel.redditor.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.reddit.RedditRestClient;
import com.exequiel.redditor.ui.fragment.adapter.SubredditPostCursorAdapter;

public class SubRedditPostListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, IProgresBarRefresher {
    View rootView;
    private static final String TAG = SubRedditPostListFragment.class.getCanonicalName();
    SubredditPostCursorAdapter subredditPostCursorAdapter;
    private RelativeLayout fSubReddit;
    private LinearLayout progressBarContainer;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    public SubRedditPostListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        if (rootView == null) {
            Log.d(TAG, "onCreateViewNotNull");
            rootView = inflater.inflate(R.layout.fragment_subreddit, container, false);
            fSubReddit = (RelativeLayout) rootView.findViewById(R.id.fSubReddits);
            progressBarContainer = (LinearLayout) fSubReddit.findViewById(R.id.progressBarContainer);
            if (savedInstanceState==null){
                Bundle subRedditsBundle = this.getArguments();
                String subreddit = "popular";
                String order = "hot";
                if (subRedditsBundle != null) {
                    subreddit = subRedditsBundle.getString(RedditContract.SubReddits.DISPLAY_NAME);
                    order = subRedditsBundle.getString(RedditContract.SubReddits.SUBREDDIT_ORDER);
                }
                /**
                 * Make a proper string value
                 */
                getActivity().setTitle("r/" + subreddit);
                new RedditRestClient(getActivity()).retrieveLinks(SubRedditPostListFragment.this, subreddit, order);

            } else{

            }
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        /**
         * La consulta tiene ser del subreddit actual
         */

        Cursor c = context.getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, null, null, null);
        ;

        subredditPostCursorAdapter = new SubredditPostCursorAdapter(context, c);
        setListAdapter(subredditPostCursorAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return LinksLoader.allLinks(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        subredditPostCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        subredditPostCursorAdapter.swapCursor(null);

    }

    @Override
    public void refresh() {
        try {

            getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "refresh()");
                    subredditPostCursorAdapter.notifyDataSetChanged();
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
    public void onDestroy() {
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        super.onDestroy();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        PostFragment fragment = new PostFragment();
        Bundle bundle = new Bundle();
        String linkId = "";
        String linkSubreddit = "";
        Cursor c = getActivity().getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, "_id =" + id, null, null);
        if (c.moveToFirst()) {
            do {
                linkId = c.getString(LinksLoader.Query._ID);
                linkSubreddit  = c.getString(LinksLoader.Query.LINK_SUBREDDIT);
            } while (c.moveToNext());
        }

        bundle.putString(RedditContract.Links._ID, linkId);
        bundle.putString(RedditContract.Links.LINK_SUBREDDIT, linkSubreddit);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.MainActivityFrameLayaout, fragment).commit();
    }


}