package org.agoenka.tweeterjam.network;

import android.content.Context;
import android.text.TextUtils;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Author: agoenka
 * Created At: 10/27/2016
 * Version: ${VERSION}
 */

/**
 * This client is responsible for communicating with a REST API.
 * Add methods for each relevant endpoint in the API.
 * Full list of supported APIs: https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 */
public class TwitterClient extends OAuthBaseClient {

    private static final Class<? extends Api> API_CLASS = TwitterApi.class;
    private static final String BASE_URL = "https://api.twitter.com/1.1/"; // Base API URL
    private static final String API_KEY = "omUIoDaf47EfRyS4Y4bDs6xPC";
    private static final String API_SECRET = "SfxnKYoNwhGuTYoKPd2iKXB7lfWpWyeUJpI3vrts6DD2sFPBqf";
    private static final String CALLBACK_URL = "https://twitter.agoenka.org"; // Matches with the data element in manifest
    public static final int PAGE_SIZE = 25;

    public TwitterClient(Context context) {
        super(context, API_CLASS, BASE_URL, API_KEY, API_SECRET, CALLBACK_URL);
    }

    public void getHomeTimeline(long maxId, long sinceId, int count, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/home_timeline.json");
        // Specify the params
        RequestParams params = new RequestParams();
        if (maxId > 0)
            params.put("max_id", maxId);
        if (sinceId > 0)
            params.put("since_id", sinceId);
        if (count > 0)
            params.put("count", count);
        // Execute the request
        getClient().get(url, params, handler);
    }

    public void getMentionsTimeline(long maxId, int count, TextHttpResponseHandler handler) {
        String url = getApiUrl("statuses/mentions_timeline.json");
        // Specify the params
        RequestParams params = new RequestParams();
        if (maxId > 0)
            params.put("max_id", maxId);
        if (count > 0)
            params.put("count", count);
        // Execute the request
        getClient().get(url, params, handler);
    }

    public void getUserTimeline(String screenName, long maxId, int count, TextHttpResponseHandler handler) {
        String url = getApiUrl("statuses/user_timeline.json");
        // Specify the params
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(screenName))
            params.put("screen_name", screenName);
        if (maxId > 0)
            params.put("max_id", maxId);
        if (count > 0)
            params.put("count", count);
        // Execute the request
        getClient().get(url, params, handler);
    }

    public void getUserCredentials(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("account/verify_credentials.json");
        getClient().get(url, handler);
    }

    public void postTweet(String status, long inReplyTo, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        if (inReplyTo > 0) {
            params.put("in_reply_to_status_id", inReplyTo);
        }
        getClient().post(url, params, handler);
    }

    public void postRetweet(long id, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/retweet/" + id + ".json");
        getClient().post(url, handler);
    }

    public void createFavorite(long id, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(url, params, handler);
    }

    public void getFollowers(String screenName, long cursor, int count, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("followers/list.json");
        // Specify the params
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(screenName))
            params.put("screen_name", screenName);
        if (cursor > 0)
            params.put("cursor", cursor);
        if (count > 0)
            params.put("count", count);
        params.put("skip_status", true);
        params.put("include_user_entities", false);
        // Execute the request
        getClient().get(url, params, handler);
    }

    public void getFriends(String screenName, long cursor, int count, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("friends/list.json");
        // Specify the params
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(screenName))
            params.put("screen_name", screenName);
        if (cursor > 0)
            params.put("cursor", cursor);
        if (count > 0)
            params.put("count", count);
        params.put("skip_status", true);
        params.put("include_user_entities", false);
        // Execute the request
        getClient().get(url, params, handler);
    }

    public void createFriend(String screenName, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("friendships/create.json");
        // Specify the params
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(screenName))
            params.put("screen_name", screenName);
        params.put("follow", true);
        // Execute the request
        getClient().post(url, params, handler);
    }
}