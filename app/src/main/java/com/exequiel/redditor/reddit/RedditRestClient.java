package com.exequiel.redditor.reddit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.exequiel.redditor.data.RedditContract;
import com.exequiel.redditor.interfaces.IOnAuthenticated;
import com.exequiel.redditor.interfaces.IProgresBarRefresher;
import com.exequiel.redditor.ui.fragment.PostFragment;
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
    private static final String CLIENT_ID = com.exequiel.redditor.BuildConfig.CLIENT_ID;
    private static final String BASE_URL = "https://www.reddit.com/api/v1/";
    private static final String BASE_URL_OAUTH = "https://oauth.reddit.com";
    private static String REDIRECT_URI = com.exequiel.redditor.BuildConfig.REDIRECT_URI;
    private static String CLIENT_SECRET = "";
    private static String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private static String GRANT_TYPE2 = "authorization_code";
    private static String ACCES_TOKEN_URL = "access_token";
    private static String OAUTH_URL = "https://www.reddit.com/api/v1/authorize.compact";
    private static String OAUTH_SCOPE = "read";
    private static String STATE = UUID.randomUUID().toString();
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
        if (isOauth) {
            return BASE_URL_OAUTH + relativeUrl;
        }
        return BASE_URL + relativeUrl;
    }

    /**
     * Rerieve the list of subrreddits by an order, should be called after getToken,
     *
     * @param order
     */
    public void retrieveSubreddits(final String order) {
        String url = "/subreddits/" + order;

        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));
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


    public void getTokenForAuthCode() throws JSONException {
        client.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String code = pref.getString("Code", "");

        RequestParams requestParams = new RequestParams();
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
                    Log.i("Access_token", pref.getString("token", ""));
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
                    Log.i("Access_token", pref.getString("token", ""));
                    iOnAuthenticated.retrieveData();
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
        //  client.addHeader("Authorization", "bearer " + pref.getString("token", ""));
        // client.addHeader("User-Agent", "Redditsavedoffline/0.1 by pratik");

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

//    public void retrieveComments(final IProgresBarRefresher progresBarRefresher, String linkSubreddit, final String linkId) {
//        Log.d(TAG, "retrieveComments");
//        final String url = "/r/" + linkSubreddit + "/comments/" + linkId;
//
//        final Header[] headers = new Header[2];
//        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
//        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));
//        get(true, url, headers, null, new JsonHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                progresBarRefresher.start_progress_bar();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d(TAG, "retrieveComments" + response);
//
//                RedditPersister.persistComments(context, linkId, response, progresBarRefresher);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d(TAG, "retrieveComments" + errorResponse);
//            }
//        });
//    }
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
            Log.d(TAG, "retrieveComments "+errorResponse);
        }
    });
}
}