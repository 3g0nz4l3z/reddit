package com.exequiel.redditor.reddit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.exequiel.redditor.R;
import com.exequiel.redditor.interfaces.IOnAuthenticated;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.interfaces.ISubscriptor;
import com.exequiel.redditor.ui.fragment.SubRedditPostListFragment;
import com.exequiel.redditor.ui.fragment.SubRedditSearchListFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Based on https://loopj.com/android-async-http/ and https://github.com/pratik98/Reddit-OAuth
 */
public class RedditRestClient {
    private static final String TAG = RedditRestClient.class.getCanonicalName();
    private static SharedPreferences pref;
    private static String token;
    private static String refresh_token;
    private static String expires_in;
    private static Context context;
    public static final String CLIENT_ID = com.exequiel.redditor.BuildConfig.CLIENT_ID;
    private static final String BASE_URL = "https://www.reddit.com/api/v1/";
    private static final String BASE_URL_OAUTH = "https://oauth.reddit.com";
    public static String REDIRECT_URI = com.exequiel.redditor.BuildConfig.REDIRECT_URI;
    private static String CLIENT_SECRET = "";
    private static String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private static String GRANT_TYPE2 = "authorization_code";
    private static String ACCES_TOKEN_URL = "access_token";
    public static String OAUTH_URL = "https://www.reddit.com/api/v1/authorize.compact";
    public static String OAUTH_SCOPE = "read mysubreddits identity save subscribe submit";
    public static String STATE = UUID.randomUUID().toString();
    private static String DEVICE_ID = STATE;
    private static String USER_AGENT = "Android/Redditor 0.1";
    private AsyncHttpClient client;

    public RedditRestClient(Context mn) {

        context = mn;
        client = new AsyncHttpClient();
    }


    public void get(boolean isOAuth, String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(isOAuth, url), headers, params, responseHandler);

    }

    public void post(boolean isOAuth, String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        client.post(context, getAbsoluteUrl(isOAuth, url), headers, params, null, responseHandler);
    }

    private String getAbsoluteUrl(boolean isOauth, String relativeUrl) {
        String absUrl = "";
        if (isOauth) {
            absUrl = BASE_URL_OAUTH + relativeUrl;
        } else {
            absUrl = BASE_URL + relativeUrl;
        }
        Log.d(TAG, "the url " + absUrl);
        return absUrl;
    }

    /**
     * Rerieve the list of subrreddits by an order, should be called after getToken,
     *
     * @param order
     */
    public void retrieveSubreddits(final String order) {
        String url = "/subreddits/" + order;
        Log.d(TAG, url);
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));
        Log.d(TAG, "retrieveSubreddits token " + headers[1]);
        get(true, url, headers, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    RedditPersister.persistSubReddits(context, order, response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "retrieveSubreddits" + errorResponse);
            }
        });
    }


    public void retrieveMySubReddits(final String order) {
        String url = "/subreddits/mine/subscriber";
        Log.d(TAG, url);
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));
        Log.d(TAG, "retrieveSubreddits token " + headers[1]);
        get(true, url, headers, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    RedditPersister.persistSubReddits(context, order, response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "retrieveSubreddits error" + errorResponse);
            }
        });
    }

    /**
     * Retrieve links for a subreddit in an order than can be controversial, hot, new, random, top
     *
     * @param progresBarRefresher
     * @param subReddit
     * @param order
     */
    public void retrieveLinks(final IProgresBarRefresher progresBarRefresher, String subReddit, final String order) {
        Log.d(TAG, "retrieveLinks");
        final String url = "/r/" + subReddit + "/" + order;

        final Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));
        Log.d(TAG, "token" + pref.getString("token", ""));
        get(true, url, headers, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                progresBarRefresher.start_progress_bar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(TAG, "retrieveLinks" + response);
                    RedditPersister.persistLinks(context, order, response, progresBarRefresher);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "retrieveLinks" + errorResponse);
            }
        });

    }


    public void getTokenForAuthCode(final IOnAuthenticated iOnAuthenticated) throws JSONException {
        client.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String code = pref.getString("Code", "");

        RequestParams requestParams = new RequestParams();
        Log.d(TAG, "code " + code);
        requestParams.put("code", code);
        requestParams.put("grant_type", GRANT_TYPE2);
        requestParams.put("redirect_uri", REDIRECT_URI);
        post(false, ACCES_TOKEN_URL, null, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("getToken()", response.toString());
                try {
                    token = response.getString("access_token").toString();
                    expires_in = response.getString("expires_in").toString();
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("token", token);
                    edit.putString("expires_in", expires_in);
                    edit.commit();
                    Log.i(TAG, "getTokenForAuthCode " + pref.getString("token", ""));
                    iOnAuthenticated.retrieveData(context.getResources().getString(R.string.default_reddit_name), true);
                } catch (JSONException j) {
                    j.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("statusCode", "" + statusCode);


            }
        });
    }

    public void getTokenFoInstalledClient(final IOnAuthenticated iOnAuthenticated) throws JSONException {
        client.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);

        RequestParams requestParams = new RequestParams();
        requestParams.put("grant_type", GRANT_TYPE);
        requestParams.put("device_id", DEVICE_ID);
        post(false, ACCES_TOKEN_URL, null, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("getToken()", response.toString());
                try {

                    token = response.getString("access_token").toString();
                    expires_in = response.getString("expires_in").toString();
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("token", token);
                    edit.putString("expires_in", expires_in);
                    edit.commit();
                    Log.i(TAG, "getTokenFoInstalledClient " + pref.getString("token", ""));
                    iOnAuthenticated.retrieveData(context.getResources().getString(R.string.default_reddit_name), false);
                } catch (JSONException j) {
                    j.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("statusCode", "" + statusCode);


            }
        });

    }


    public void revokeToken() {
        client.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String access_token = pref.getString("token", "");

        RequestParams requestParams = new RequestParams();
        requestParams.put("token", access_token);
        requestParams.put("token_type_hint", "access_token");

        post(false, "revoke_token", null, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("response", response.toString());
                SharedPreferences.Editor edit = pref.edit();
                edit.remove(token);
                edit.commit();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("statusCode", "" + statusCode);
            }
        });
    }

    public void getUsername() {
        Log.i("token", pref.getString("token", ""));

        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));
        String url = "/me";
        get(true, url, headers, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("response", response.toString());
                try {
                    String username = response.getString("name").toString();
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("username", username);
                    edit.commit();
                } catch (JSONException j) {
                    j.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("response", errorResponse.toString());
                Log.i("statusCode", "" + statusCode);
            }
        });
    }

    public void retrieveComments(final IProgresBarRefresher progresBarRefresher, String linkSubreddit, final String linkId) {
        final String url = "/r/" + linkSubreddit + "/comments/" + linkId;

        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));
        Log.d(TAG, url);
        get(true, url, headers, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                progresBarRefresher.start_progress_bar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "onSuccess");
                try {
                    RedditPersister.persistComments(context, linkId, response, progresBarRefresher);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "retrieveComments " + errorResponse);
            }
        });
    }

    public void subscribeSubreddit(String subRedditName, final ISubscriptor iSubscriptor) {
        Log.d(TAG, "subscribeSubreddit");
        final String url = "/api/subscribe";

        final Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));

        RequestParams par = new RequestParams();
        par.put("action", "sub");
        par.put("sr_name", subRedditName);

        Log.d(TAG, "token" + pref.getString("token", ""));
        post(true, url, headers, par, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    iSubscriptor.callSubscriveLogic();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "retrieveLinks" + errorResponse);
            }
        });

    }

    public void unSubscribeSubreddit(String subRedditName, final ISubscriptor iSubscriptor) {
        Log.d(TAG, "unSubscribeSubreddit");
        final String url = "/api/subscribe";

        final Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));

        RequestParams par = new RequestParams();
        par.put("action", "unsub");
        par.put("sr_name", subRedditName);

        Log.d(TAG, "token" + pref.getString("token", ""));
        post(true, url, headers, par, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                iSubscriptor.callSubscriveLogic();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "retrieveLinks" + errorResponse);
            }
        });
    }



    public void commentPost(String thing_id, String text) {
        Log.d(TAG, "unSubscribeSubreddit");
        final String url = "/api/comment";

        final Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));

        RequestParams par = new RequestParams();
        par.put("api_type", "json");
        par.put("text", text);
        par.put("thing_id", thing_id);

        Log.d(TAG, "token" + pref.getString("token", ""));
        post(true, url, headers, par, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "commentPost" + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "commentPost" + errorResponse);
            }
        });
    }

    public void searchSubredditName(String query, final IProgresBarRefresher iProgresBarRefresher) {
        Log.d(TAG, "searchSubredditName");
        // final String url = "/api/search_reddit_names";
        final String url = "/subreddits/search?q="+query;

        final Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));
//
//        RequestParams par = new RequestParams();
//        par.put("exact", "false");
//        par.put("query", query);
//        par.put("include_over_18", "false");

        Log.d(TAG, "token" + pref.getString("token", ""));
        get(true, url, headers, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "searchSubredditName "+response);
                try {
                    RedditPersister.persistSearch(context, response, iProgresBarRefresher);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "searchSubredditName " + errorResponse);
            }
        });

    }
}