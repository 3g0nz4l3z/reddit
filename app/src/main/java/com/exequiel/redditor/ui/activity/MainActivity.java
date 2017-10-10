package com.exequiel.redditor.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.exequiel.redditor.BuildConfig;
import com.exequiel.redditor.R;
import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.data.SubRedditLoader;
import com.exequiel.redditor.interfaces.*;
import com.exequiel.redditor.reddit.RedditRestClient;
import com.exequiel.redditor.ui.fragment.SubRedditPostListFragment;
import com.exequiel.redditor.ui.fragment.SubRedditSearchListFragment;
import com.exequiel.redditor.ui.fragment.SubRedditsNameListFragment;

import org.json.JSONException;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Based on https://github.com/pratik98/Reddit-OAuth for the login
 */
public class MainActivity extends AppCompatActivity implements IOnAuthenticated {
    private static final String TAG = MainActivity.class.getCanonicalName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textViewLinksTitle)
    TextView textViewTitle;
    @BindView(R.id.searchViewSubreddits)
    SearchView searchView;
    FragmentManager fm;
    RedditRestClient redditRestClient;
    FragmentTransaction ft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        redditRestClient = new RedditRestClient(MainActivity.this);
        try {
            redditRestClient.getTokenFoInstalledClient(MainActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                SubRedditSearchListFragment subRedditSearchListFragment = new SubRedditSearchListFragment();
                new RedditRestClient(MainActivity.this).searchSubredditName(query, subRedditSearchListFragment);
                ft.replace(R.id.MainActivityFrameLayaout, subRedditSearchListFragment).commit();
                searchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public void retrieveData(final String type, final boolean isUser) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RedditRestClient redditRestClient = new RedditRestClient(MainActivity.this);
                if (isUser) {
                    redditRestClient.retrieveMySubReddits(type);
                    redditRestClient.getUsername();
                } else {
                    Log.d(TAG, "retrieveData");
                    redditRestClient.retrieveSubreddits(type);
                }
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();

                ft.replace(R.id.MainActivityFrameLayaout, new SubRedditPostListFragment()).commit();
                ft = fm.beginTransaction();

                ft.replace(R.id.SubRedditsNameListFragment, new SubRedditsNameListFragment()).commit();
            }
        });

    }
}
