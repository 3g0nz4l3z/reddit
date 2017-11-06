package com.exequiel.redditor.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.analytics.AnalyticsApplication;
import com.exequiel.redditor.data.LinksLoader;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.data.SubRedditLoader;
import com.exequiel.redditor.interfaces.IOnAuthenticated;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.interfaces.ISubscriptor;
import com.exequiel.redditor.reddit.RedditRestClient;
import com.exequiel.redditor.ui.activity.MainActivity;
import com.exequiel.redditor.ui.activity.PostActivity;
import com.exequiel.redditor.ui.fragment.adapter.SubredditPostCursorAdapter;
import com.exequiel.redditor.ui.widget.SubredditAppWidgetProvider;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;

public class SubRedditPostListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, IProgresBarRefresher, ISubscriptor {
    View rootView;
    private static final String TAG = SubRedditPostListFragment.class.getCanonicalName();
    SubredditPostCursorAdapter subredditPostCursorAdapter;
    private CoordinatorLayout fSubReddit;
    private LinearLayout progressBarContainer;
    private FloatingActionButton fab;
    boolean bLogIn;
    private boolean bSubscrived;
    SharedPreferences pref;
    private String authCode;
    Dialog auth_dialog;

    Intent resultIntent = new Intent();
    private boolean authComplete;
    private IOnAuthenticated mCallback;
    static String subreddit = "popular";
    static String order = "hot";

    private Tracker mTracker;
    static String prefixed_name = "r/popular";

    private void initGAnalytics() {
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void gASendSubscribed(String subreddit){
        Log.d(TAG, "gASendSubscribed");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Subscribed")
                .setAction(subreddit)
                .build());
    }
    private void gASendUnsubscribed(String subreddit){
        Log.d(TAG, "gASendUnsubscribed");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("unsubscribed")
                .setAction(subreddit)
                .build());

    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    public SubRedditPostListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void logInFabColorLogic() {

        pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);
        bLogIn = pref.getBoolean("logIn", false);
        if (bLogIn) {
            fab.setImageResource(R.drawable.ic_subscrived);
            bSubscrived = isSubscrived();
            if (bSubscrived) {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.colorAccent)));
                fab.setContentDescription(getString(R.string.subscriveButton));
            } else {
                fab.setImageResource(R.drawable.ic_unsubscrived);
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.colorWhite)));
                fab.setContentDescription(getString(R.string.unSubscriveButton));
            }
        } else {
            fab.setImageResource(R.drawable.ic_input_white_24px);
        }


    }

    private boolean isSubscrived() {
        Cursor cResult = getActivity().getContentResolver().query(RedditContract.SubReddits.CONTENT_URI, SubRedditLoader.Query.PROJECTION, RedditContract.SubReddits.DISPLAY_NAME + " = \"" + subreddit + "\"", null, null);
        Log.d(TAG, "isSubscrived " + (cResult.getCount() > 0));

        return cResult.getCount() > 0;
    }

    private void logIn() {

        auth_dialog = new Dialog(getActivity());
        auth_dialog.setContentView(R.layout.auth_dialog);
        WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
        web.getSettings().setJavaScriptEnabled(true);
        String url = RedditRestClient.OAUTH_URL + "?client_id=" + RedditRestClient.CLIENT_ID + "&response_type=code&state=" + RedditRestClient.STATE + "&redirect_uri=" + RedditRestClient.REDIRECT_URI + "&scope=" + RedditRestClient.OAUTH_SCOPE;
        web.loadUrl(url);


        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


                if (url.contains("?code=") || url.contains("&code=")) {
                    Log.d("OnPageFinished", url.toString());
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i("OnPageFinished", "CODE : " + authCode);
                    authComplete = true;
                    resultIntent.putExtra("code", authCode);
                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                    getActivity().setResult(Activity.RESULT_CANCELED, resultIntent);

                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("Code", authCode);
                    edit.putBoolean("logIn", true);
                    edit.commit();
                    fab.setImageResource(R.drawable.ic_subscrived);
                    auth_dialog.dismiss();

                    try {
                        new RedditRestClient(getActivity()).getTokenForAuthCode(mCallback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    resultIntent.putExtra("code", authCode);
                    authComplete = true;
                    getActivity().setResult(Activity.RESULT_CANCELED, resultIntent);

                    auth_dialog.dismiss();
                }
            }
        });
        auth_dialog.show();
        auth_dialog.setTitle("Authorize");
        auth_dialog.setCancelable(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("subreddit", subreddit);
        savedInstanceState.putString("order", order);
        savedInstanceState.putString("prefixed_name", prefixed_name);

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        initGAnalytics();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_subreddit, container, false);
            fSubReddit = (CoordinatorLayout) rootView.findViewById(R.id.fSubReddits);
            fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

            fab.setContentDescription(getString(R.string.logInButton));
            progressBarContainer = (LinearLayout) fSubReddit.findViewById(R.id.progressBarContainer);
            if (savedInstanceState == null) {
                Bundle subRedditsBundle = this.getArguments();
                if (subRedditsBundle != null) {
                    subreddit = subRedditsBundle.getString(RedditContract.SubReddits.DISPLAY_NAME);
                    prefixed_name = subRedditsBundle.getString(RedditContract.SubReddits.DISPLAY_NAME_PREFIXED);
                    order = subRedditsBundle.getString(RedditContract.SubReddits.SUBREDDIT_ORDER);
                }
            }
                logInFabColorLogic();

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bLogIn) {
                            bSubscrived = isSubscrived();
                            if (!bSubscrived) {
                                new RedditRestClient(getActivity()).subscribeSubreddit(subreddit, SubRedditPostListFragment.this);
                                gASendSubscribed(subreddit);
                            } else {
                                new RedditRestClient(getActivity()).unSubscribeSubreddit(subreddit, SubRedditPostListFragment.this);
                                gASendUnsubscribed(subreddit);
                            }
                        } else {
                            logIn();
                        }

                    }
                });
                /**
                 * Make a proper string value
                 */
                new RedditRestClient(getActivity()).retrieveLinks(SubRedditPostListFragment.this, subreddit, order);

            }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();

        if (savedInstanceState!=null)
        {
            subreddit = savedInstanceState.getString("subreddit", subreddit);
            order = savedInstanceState.getString("order", order);
            prefixed_name = savedInstanceState.getString("prefixed_name", prefixed_name);
        }

        TextView textViewTitle = (TextView) getActivity().findViewById(R.id.textViewLinksTitle);
        textViewTitle.setText(prefixed_name);
        Cursor c = context.getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, null, null, null);

        subredditPostCursorAdapter = new SubredditPostCursorAdapter(context, c);
        setListAdapter(subredditPostCursorAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        this.refresh();
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
                    try{

                        progressBarContainer.setVisibility(View.GONE);
                    }catch(NullPointerException e){

                    }


                    Intent refreshIntent = new Intent(getContext(), SubredditAppWidgetProvider.class);
                    refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    getActivity().sendBroadcast(refreshIntent);
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void start_progress_bar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "start_progress_bar");
                try{
                progressBarContainer.setVisibility(View.VISIBLE);
            }catch(NullPointerException e){

            }
            }
        });

    }

    @Override
    public void end_progress_bar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "end_progress_bar");
                try{
                progressBarContainer.setVisibility(View.GONE);
                }catch(NullPointerException e){

                }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (IOnAuthenticated) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IProgresBarRefresher");
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        String linkId = "";
        String linkLinkId = "";
        String linkSubreddit = "";
        Cursor c = getActivity().getContentResolver().query(RedditContract.Links.CONTENT_URI, LinksLoader.Query.PROJECTION, "_id =" + id, null, null);
        if (c.moveToFirst()) {
            do {
                linkId = c.getString(LinksLoader.Query._ID);
                linkLinkId = c.getString(LinksLoader.Query.LINK_ID);
                linkSubreddit = c.getString(LinksLoader.Query.LINK_SUBREDDIT);
            } while (c.moveToNext());
        }

        Intent intent = new Intent(getActivity(), PostActivity.class);
        intent.putExtra(RedditContract.Links._ID, linkId);
        intent.putExtra(RedditContract.Links.LINK_ID, linkLinkId);
        intent.putExtra(RedditContract.Links.LINK_SUBREDDIT, linkSubreddit);

        getActivity().startActivity(intent);


    }


    @Override
    public void callSubscriveLogic() {
        new RedditRestClient(getActivity()).retrieveMySubReddits("poular", SubRedditPostListFragment.this);
    }
}