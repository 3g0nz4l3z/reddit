package com.exequiel.redditor.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.ui.fragment.PostFragment;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        PostFragment fragment = new PostFragment();
//        Bundle bundle = new Bundle();
//        String linkId = savedInstanceState.getString(RedditContract.Links._ID);
//        String linkLinkId = savedInstanceState.getString(RedditContract.Links.LINK_ID);
//        String linkSubreddit = savedInstanceState.getString(RedditContract.Links.LINK_SUBREDDIT);
//
//        bundle.putString(RedditContract.Links._ID, linkId);
//        bundle.putString(RedditContract.Links.LINK_ID, linkLinkId);
//        bundle.putString(RedditContract.Links.LINK_SUBREDDIT, linkSubreddit);
//        fragment.setArguments(bundle);
//        PostActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.MainActivityFrameLayaout, fragment).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
