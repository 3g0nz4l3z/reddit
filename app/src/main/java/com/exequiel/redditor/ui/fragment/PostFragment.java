package com.exequiel.redditor.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.reddit.RedditRestClient;
import com.exequiel.redditor.ui.fragment.adapter.CommentsCursorAdapter;


public class PostFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, IProgresBarRefresher {
    View rootView;
    private static final String TAG = PostFragment.class.getCanonicalName();
    CommentsCursorAdapter commentsSimpleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_post, container, false);
            if (savedInstanceState == null) {
                Bundle bundle = this.getArguments();
                String linkId;
                String linkSubreddit;
                if (bundle != null){
                    linkId = bundle.getString(RedditContract.Links.LINK_ID);
                    linkSubreddit = bundle.getString(RedditContract.Links.LINK_ID);
                    new RedditRestClient(getActivity()).retrieveComments(PostFragment.this, linkSubreddit, linkId);
                }
            }
        }
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void start_progress_bar() {

    }

    @Override
    public void end_progress_bar() {

    }

    public void onDestroy() {
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        super.onDestroy();

    }
}
