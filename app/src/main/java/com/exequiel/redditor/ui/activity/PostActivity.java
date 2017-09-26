package com.exequiel.redditor.ui.activity;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.CommentsLoader;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.reddit.RedditRestClient;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends AppCompatActivity implements IProgresBarRefresher {

    private static final String TAG = "PostActivity";
    @BindView(R.id.progressBarContainer)
    LinearLayout progressBarContainer;
    @BindView(R.id.linearLayaoutComments)
    LinearLayout linearLayaoutComments;
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
    Cursor cComments;
    String sLinkId;
    String sSubreddit;
    String sDomain;
    String sLinkTitle;
    String sLinkComments;
    String sLinkPoints;
    String sLinkImage;
    RedditRestClient redditRestClient;
    private String sSubredditName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        redditRestClient = new RedditRestClient(PostActivity.this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
                    } while (cLink.moveToNext());
                }
                Log.d(TAG, sSubreddit + " " + sDomain + " " + sLinkTitle + " " + sLinkComments + " " + sLinkPoints + " " + sLinkImage);
                textViewSubrredit.setText(sSubreddit);
                textViewDomain.setText(sDomain);
                textViewLinkTitle.setText(sLinkTitle);
                textViewLinkComments.setText(sLinkComments);
                textViewLinkPoints.setText(sLinkPoints);
                redditRestClient.retrieveComments(PostActivity.this, sSubredditName, sLinkId);
                try {
                    if (!sLinkImage.equals(null)) {
                        Log.d(TAG, sLinkImage);
                        Picasso.with(PostActivity.this).load(sLinkImage).into(imageViewLink);
                    } else {
                        imageViewLink.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    imageViewLink.setVisibility(View.INVISIBLE);
                }
            }

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
//                    int backColor = getColorBackground(viewComment);

                  //  if (viewComment.getBackground())
                    LinearLayout childComment = (LinearLayout) viewComment.findViewById(R.id.lineaLayOutChildComment);
                    TextView textViewUserName = (TextView) viewComment.findViewById(R.id.textViewUserName);
                    TextView textViewCommentScore = (TextView) viewComment.findViewById(R.id.textViewCommentScore);
                    TextView textViewBody = (TextView) viewComment.findViewById(R.id.textViewBody);


                    String parentId = cursor.getString(CommentsLoader.Query.COMMENTS_PARENT_ID);
                    String sUserName = cursor.getString(CommentsLoader.Query.COMMENTS_AUTHOR);
                    String sCommentScore= cursor.getString(CommentsLoader.Query.COMMENTS_SCORE);
                    String sBody= cursor.getString(CommentsLoader.Query.COMMENTS_BODY);
                    textViewUserName.setText(sUserName);
                    textViewCommentScore.setText(sCommentScore);
                    textViewBody.setText(sBody);
                    Cursor internalCursor = PostActivity.this.getContentResolver().query(RedditContract.Comments.CONTENT_URI, CommentsLoader.Query.PROJECTION, RedditContract.Comments.COMMENTS_PARENT_ID + " =\"" + parentId + "\"", null, null);
                    linearLayaoutComments.addView(viewComment);
                    if (bChangeColor){
                        addCommentsView(internalCursor, childComment, false);
                        viewComment.setBackgroundResource(R.color.colorPrimaryLight);
                    }else{
                        addCommentsView(internalCursor, childComment, true);
                        viewComment.setBackgroundResource(R.color.colorWhite);
                    }
                } while (cursor.moveToNext());
            }

        }catch(Exception e){

        }
    }

    private int getColorBackground(View view){
        int backgroundColor = 0;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable)
            backgroundColor = ((ColorDrawable) background).getColor();
        return backgroundColor;
    }
}
