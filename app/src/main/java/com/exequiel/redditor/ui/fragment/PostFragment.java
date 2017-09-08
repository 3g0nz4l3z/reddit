package com.exequiel.redditor.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.reddit.RedditRestClient;
import com.exequiel.redditor.ui.fragment.adapter.CommentsCursorAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PostFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, IProgresBarRefresher {
    View rootView;
    @BindView(R.id.textViewSubrredit)
    TextView textViewSubrredit;
    @BindView(R.id.textViewDomain)
    TextView textViewDomain;
    @BindView(R.id.textViewLinkTitle)
    TextView textViewLinkTitle;
    @BindView(R.id.textViewLinkPoints)
    TextView textViewLinkPoints;
    @BindView(R.id.textViewLinkComments)
    TextView textViewLinkComments;
    @BindView(R.id.link_body_layaout)
    LinearLayout link_body;
    @BindView(R.id.link_image_layaout)
    LinearLayout link_image;

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
            ButterKnife.bind(this, rootView);
            String linkId = "";
            String linkSubreddit = "";
            if (savedInstanceState == null) {
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    linkId = bundle.getString(RedditContract.Links._ID);
                    linkSubreddit = bundle.getString(RedditContract.Links.LINK_SUBREDDIT);
                    Log.d(TAG, linkId + " " + linkSubreddit);
                    new RedditRestClient(getActivity()).retrieveComments(PostFragment.this, linkSubreddit, linkId);
                }
            }
            Cursor c = getActivity().getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, RedditContract.Links._ID + " = " + linkId, null, null);
            if (c.moveToFirst()) {
                do {
                    Log.d(TAG, "PostFragment");
                    textViewSubrredit.setText(c.getString(LinksLoader.Query.LINK_SUBREDDIT_NAME_PREFIXED));
                    textViewDomain.setText(c.getString(LinksLoader.Query.LINK_DOMAIN));
                    textViewLinkTitle.setText(c.getString(LinksLoader.Query.LINK_TITLE));
                    textViewLinkPoints.setText(c.getString(LinksLoader.Query.LINK_SCORE));
                    textViewLinkComments.setText(c.getString(LinksLoader.Query.LINK_NUM_COMMENTS));
                } while (c.moveToNext());
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
