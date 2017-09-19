package com.exequiel.redditor.ui.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.textViewSubrredit)
    TextView textViewSubrredit;
    @BindView(R.id.textViewDomain)
    TextView textViewDomain;
    @BindView(R.id.textViewLinkTitle)
    TextView textViewLinkTitle;
    @BindView(R.id.textViewLinkComments)
    TextView textViewLinkComments;
    @BindView(R.id.textViewLinkPoints)
    TextView textViewLinkPoints;
    @BindView(R.id.imageViewLink)
    ImageView imageViewLink;
    Cursor cLink;
    String sLINK_ID;
    String sSubreddit;
    String sDomain;
    String sLinkTitle;
    String sLinkComments;
    String sLinkPoints;
    String sLinkImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        if (savedInstanceState==null){
            Bundle extras = getIntent().getExtras();
            if (extras!=null){
                sLINK_ID = extras.getString(RedditContract.Links.LINK_ID);
                cLink = PostActivity.this.getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, RedditContract.Links.LINK_ID+" =\"" + sLINK_ID+"\"", null, null);
                if (cLink.moveToFirst()) {
                    do {
                        sSubreddit = cLink.getString(LinksLoader.Query.LINK_SUBREDDIT_NAME_PREFIXED);
                        sDomain = cLink.getString(LinksLoader.Query.LINK_DOMAIN);
                        sLinkTitle  = cLink.getString(LinksLoader.Query.LINK_TITLE);
                        sLinkComments  = cLink.getString(LinksLoader.Query.LINK_NUM_COMMENTS);
                        sLinkPoints  = cLink.getString(LinksLoader.Query.LINK_SCORE);
                        sLinkImage  = cLink.getString(LinksLoader.Query.LINK_IMAGE);
                    } while (cLink.moveToNext());
                }
                Log.d(TAG, sSubreddit+ " "+sDomain+" "+sLinkTitle+" "+sLinkComments+" "+sLinkPoints+" "+sLinkImage );
                textViewSubrredit.setText(sSubreddit);
                textViewDomain.setText(sDomain);
                textViewLinkTitle.setText(sLinkTitle);
                textViewLinkComments.setText(sLinkComments);
                textViewLinkPoints.setText(sLinkPoints);
                Log.d(TAG, sLinkImage);
                if (!sLinkImage.equals(null)){
                    Picasso.with(PostActivity.this).load(sLinkImage).into(imageViewLink);
                }
            }

        }
    }
}
