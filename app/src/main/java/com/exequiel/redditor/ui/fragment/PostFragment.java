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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.CommentsLoader;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.reddit.RedditRestClient;
import com.exequiel.redditor.ui.fragment.adapter.CommentsCursorAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PostFragment extends Fragment implements IProgresBarRefresher {
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
    @BindView(R.id.ListViewComments)
    ExpandableListView expandableListViewComments;

    private static final String TAG = PostFragment.class.getCanonicalName();
    CommentsCursorAdapter commentsAdapter;

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
            String linkLinkId = "";
            if (savedInstanceState == null) {
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    linkId = bundle.getString(RedditContract.Links._ID);
                    linkSubreddit = bundle.getString(RedditContract.Links.LINK_SUBREDDIT);
                    linkLinkId = bundle.getString(RedditContract.Links.LINK_ID);;
                    Log.d(TAG, linkId + " " + linkSubreddit);
                    new RedditRestClient(getActivity()).retrieveComments(PostFragment.this, linkSubreddit, linkLinkId);
                }
            }
            Log.d(TAG, linkLinkId);
            Cursor cLink = getActivity().getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, RedditContract.Links._ID + " = \"" + linkId+"\"", null, null);
            if (cLink.moveToFirst()) {
                do {
                    Log.d(TAG, "PostFragment");
                    textViewSubrredit.setText(cLink.getString(LinksLoader.Query.LINK_SUBREDDIT_NAME_PREFIXED));
                    textViewDomain.setText(cLink.getString(LinksLoader.Query.LINK_DOMAIN));
                    textViewLinkTitle.setText(cLink.getString(LinksLoader.Query.LINK_TITLE));
                    textViewLinkPoints.setText(cLink.getString(LinksLoader.Query.LINK_SCORE));
                    textViewLinkComments.setText(cLink.getString(LinksLoader.Query.LINK_NUM_COMMENTS));
                } while (cLink.moveToNext());
            }
        }


        Cursor cComments = getActivity().getContentResolver().query(RedditContract.Comments.CONTENT_URI, CommentsLoader.Query.PROJECTION, null, null, null);
        commentsAdapter = new CommentsCursorAdapter(cComments, getContext());
        expandableListViewComments.setAdapter(commentsAdapter);
        return rootView;
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
