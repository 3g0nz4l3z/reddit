package com.exequiel.redditor.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.exequiel.redditor.R;
import com.exequiel.redditor.data.CommentsLoader;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.reddit.RedditRestClient;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends OnAppCloseActivity implements IProgresBarRefresher {

    private static final String TAG = "PostActivity";
    private static final String YOUTUBE = "youtube.com";
    private static final String YOUTU_BE = "youtu.be";
    @BindView(R.id.progressBarContainer)
    LinearLayout progressBarContainer;
    @BindView(R.id.linearLayaoutComments)
    LinearLayout linearLayaoutComments;
    @BindView(R.id.fab_share)
    FloatingActionButton fab_share;
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
    @BindView(R.id.photo_container)
    FrameLayout photoContainer;
    @BindView(R.id.imageButtonPlayMedia)
    ImageButton playMedia;
    Dialog commentDialog;
    Cursor cLink;
    static Cursor cComments;
    static String sLinkId;
    static String sSubreddit;
    static String sDomain;
    static String sLinkTitle;
    static String sLinkComments;
    static String sLinkPoints;
    static String sLinkImage;
    RedditRestClient redditRestClient;
    static String sSubredditName;
    static String sLinkUrl;


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "savedInstance Not Null");
        sSubreddit = savedInstanceState.getString("sSubreddit", sSubreddit);
        sSubredditName = savedInstanceState.getString("sSubredditName", sSubredditName);
        sDomain = savedInstanceState.getString("sDomain", sDomain);
        sLinkTitle = savedInstanceState.getString("sLinkTitle", sLinkTitle);
        sLinkComments = savedInstanceState.getString("sLinkComments", sLinkComments);
        sLinkPoints = savedInstanceState.getString("sLinkPoints", sLinkPoints);
        sLinkImage = savedInstanceState.getString("sLinkImage", sLinkImage);
        sLinkUrl = savedInstanceState.getString("sLinkUrl", sLinkUrl);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("sSubreddit", sSubreddit);
        savedInstanceState.putString("sSubredditName", sSubredditName);
        savedInstanceState.putString("sDomain", sDomain);
        savedInstanceState.putString("sLinkTitle", sLinkTitle);
        savedInstanceState.putString("sLinkComments", sLinkComments);
        savedInstanceState.putString("sLinkPoints", sLinkPoints);
        savedInstanceState.putString("sLinkImage", sLinkImage);
        savedInstanceState.putString("sLinkUrl", sLinkUrl);

        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        redditRestClient = new RedditRestClient(PostActivity.this);

        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType(getString(R.string.type));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TITLE, sLinkTitle);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sLinkUrl);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
            }
        });


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                sLinkId = extras.getString(RedditContract.Links.LINK_ID);
                cLink = PostActivity.this.getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, RedditContract.Links.LINK_ID + " =\"" + sLinkId + "\"", null, null);
                if (cLink.moveToFirst()) {
                    do {
                        sSubreddit = cLink.getString(LinksLoader.Query.LINK_SUBREDDIT_NAME_PREFIXED);
                        sSubredditName = cLink.getString(LinksLoader.Query.LINK_SUBREDDIT);
                        sDomain = cLink.getString(LinksLoader.Query.LINK_DOMAIN);
                        sLinkTitle = cLink.getString(LinksLoader.Query.LINK_TITLE);
                        sLinkComments = cLink.getString(LinksLoader.Query.LINK_NUM_COMMENTS);
                        sLinkPoints = cLink.getString(LinksLoader.Query.LINK_SCORE);
                        sLinkImage = cLink.getString(LinksLoader.Query.LINK_IMAGE);
                        sLinkUrl = cLink.getString(LinksLoader.Query.LINK_URL);
                    } while (cLink.moveToNext());
                }
                Log.d(TAG, sSubreddit + " " + sDomain + " " + sLinkTitle + " " + sLinkComments + " " + sLinkPoints + " " + sLinkImage);

            }

        }

        Log.d(TAG, sSubreddit + "" + sDomain);
        textViewSubrredit.setText(sSubreddit);
        textViewDomain.setText(sDomain);
        textViewLinkTitle.setText(sLinkTitle);
        textViewLinkComments.setText(sLinkComments);
        textViewLinkPoints.setText(sLinkPoints);
        redditRestClient.retrieveComments(PostActivity.this, sSubredditName, sLinkId);

        try {
            if (sLinkUrl.contains(YOUTU_BE) || sLinkUrl.contains(YOUTUBE)) {
                playMedia.setVisibility(View.VISIBLE);
            }


            playMedia.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sLinkUrl)));
                }
            });
        } catch (NullPointerException e) {

        }


        try {

            Glide.with(PostActivity.this).load(sLinkImage).into(imageViewLink);
        } catch (Exception e) {
            photoContainer.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void refresh() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "refresh()");
                Log.d(TAG, sLinkId);
                cComments = PostActivity.this.getContentResolver().query(RedditContract.Comments.CONTENT_URI, CommentsLoader.Query.PROJECTION, RedditContract.Comments.COMMENTS_LINK_ID + " like \"%" + sLinkId + "\"", null, null);
                addCommentsView(cComments, linearLayaoutComments, false);
                // add comments here in a form
                progressBarContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void start_progress_bar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "start_progress_bar");
                progressBarContainer.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void end_progress_bar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "end_progress_bar");
                progressBarContainer.setVisibility(View.GONE);
            }
        });
    }

    private void addCommentsView(Cursor cursor, LinearLayout linearLayaoutComments, boolean bChangeColor) {
        try {
            if (cursor.moveToFirst()) {
                do {

                    View viewComment = LayoutInflater.from(PostActivity.this).inflate(R.layout.comment_item, null);

                    LinearLayout childComment = (LinearLayout) viewComment.findViewById(R.id.lineaLayOutChildComment);
                    TextView textViewUserName = (TextView) viewComment.findViewById(R.id.textViewUserName);
                    TextView textViewCommentScore = (TextView) viewComment.findViewById(R.id.textViewCommentScore);
                    TextView textViewBody = (TextView) viewComment.findViewById(R.id.textViewBody);


                    String parentId = cursor.getString(CommentsLoader.Query.COMMENTS_PARENT_ID);
                    String sUserName = cursor.getString(CommentsLoader.Query.COMMENTS_AUTHOR);
                    String sCommentScore = cursor.getString(CommentsLoader.Query.COMMENTS_SCORE);
                    String sBody = cursor.getString(CommentsLoader.Query.COMMENTS_BODY);
                    textViewUserName.setText(sUserName);
                    textViewCommentScore.setText(sCommentScore);
                    textViewBody.setText(sBody);
                    Cursor internalCursor = PostActivity.this.getContentResolver().query(RedditContract.Comments.CONTENT_URI, CommentsLoader.Query.PROJECTION, RedditContract.Comments.COMMENTS_PARENT_ID + " =\"" + parentId + "\"", null, null);
                    linearLayaoutComments.addView(viewComment);
                    if (bChangeColor) {
                        addCommentsView(internalCursor, childComment, false);
                        viewComment.setBackgroundResource(R.color.colorPrimaryLight);
                    } else {
                        addCommentsView(internalCursor, childComment, true);
                        viewComment.setBackgroundResource(R.color.colorWhite);
                    }
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {

        }
    }

    private int getColorBackground(View view) {
        int backgroundColor = 0;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable)
            backgroundColor = ((ColorDrawable) background).getColor();
        return backgroundColor;
    }
}
