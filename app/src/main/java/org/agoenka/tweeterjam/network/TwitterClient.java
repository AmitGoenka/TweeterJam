package org.agoenka.tweeterjam.network;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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

    public TwitterClient(Context context) {
        super(context, API_CLASS, BASE_URL, API_KEY, API_SECRET, CALLBACK_URL);
    }

    public void getHomeTimeline(int count, long maxId, long sinceId, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/home_timeline.json");
        // Specify the params
        RequestParams params = new RequestParams();
        if (count > 0)
            params.put("count", count);
        if (maxId > 0)
            params.put("max_id", maxId);
        if (sinceId > 0)
            params.put("since_id", sinceId);
        // Execute the request
        getClient().get(url, params, handler);
    }

    public void getUserCredentials(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("account/verify_credentials.json");
        getClient().get(url, handler);
    }
}