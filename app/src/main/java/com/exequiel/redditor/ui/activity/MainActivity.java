package com.exequiel.redditor.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.exequiel.redditor.BuildConfig;
import com.exequiel.redditor.R;
import com.exequiel.redditor.interfaces.*;
import com.exequiel.redditor.reddit.RedditRestClient;
import com.exequiel.redditor.ui.fragment.SubRedditPostListFragment;

import org.json.JSONException;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Based on https://github.com/pratik98/Reddit-OAuth for the login
 */
public class MainActivity extends AppCompatActivity implements IOnAuthenticated {
    private static final String TAG = MainActivity.class.getCanonicalName();
    SharedPreferences pref;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Dialog auth_dialog;
    String authCode;
    boolean authComplete = false;
    Intent resultIntent = new Intent();
    FragmentManager fm;
    FragmentTransaction ft;
    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    private static String CLIENT_SECRET = "";
    private static String REDIRECT_URI = BuildConfig.REDIRECT_URI;
    private static String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private static String GRANT_TYPE2 = "authorization_code";
    private static String TOKEN_URL = "access_token";
    private static String OAUTH_URL = "https://www.reddit.com/api/v1/authorize.compact";
    private static String OAUTH_SCOPE = "read";
    private static String STATE = UUID.randomUUID().toString();
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        final RedditRestClient redditRestClient = new RedditRestClient(MainActivity.this);
        try {
            redditRestClient.getTokenFoInstalledClient(MainActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth_dialog = new Dialog(MainActivity.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                String url = OAUTH_URL + "?client_id=" + CLIENT_ID + "&response_type=code&state=" + STATE + "&redirect_uri=" + REDIRECT_URI + "&scope=" + OAUTH_SCOPE;
                web.loadUrl(url);
                Toast.makeText(getApplicationContext(), "" + url, Toast.LENGTH_LONG).show();

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
                            MainActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("Code", authCode);
                            edit.commit();
                            auth_dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authorization Code is: " + pref.getString("Code", ""), Toast.LENGTH_SHORT).show();

                            try {
                                redditRestClient.getTokenForAuthCode();
                                Toast.makeText(getApplicationContext(), "Auccess Token: " + pref.getString("token", ""), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (url.contains("error=access_denied")) {
                            Log.i("", "ACCESS_DENIED_HERE");
                            resultIntent.putExtra("code", authCode);
                            authComplete = true;
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

                            auth_dialog.dismiss();
                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("Authorize");
                auth_dialog.setCancelable(true);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void retrieveData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new RedditRestClient(MainActivity.this).retrieveSubreddits("popular");
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.add(R.id.MainActivityFrameLayaout, new SubRedditPostListFragment()).commit();

            }
        });

    }
}
