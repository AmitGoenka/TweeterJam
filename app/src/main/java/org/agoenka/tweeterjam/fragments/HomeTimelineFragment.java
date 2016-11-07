package org.agoenka.tweeterjam.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.network.TwitterClient;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.network.TwitterClient.PAGE_SIZE;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;

/**
 * Author: agoenka
 * Created At: 11/5/2016
 * Version: ${VERSION}
 */
public class HomeTimelineFragment extends TweetsListFragment {

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TweeterJamApplication.getTwitterClient();
    }

    // Send an API request to get the timeline json
    // Fill the list view by creating the tweet objects from json
    @Override
    public void populateTimeline(final long maxId, final boolean refresh) {
        if (isConnected(getContext())) {
            client.getHomeTimeline(maxId, 0, PAGE_SIZE, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String json) {
                    Log.d("DEBUG", json);
                    List<Tweet> tweets = getGson().fromJson(json, new TypeToken<List<Tweet>>(){}.getType());
                    addAll(tweets, refresh);
                    Tweet.save(tweets);
                    if(loadingListener != null) loadingListener.onLoad(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("DEBUG", responseString);
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    if(loadingListener != null) loadingListener.onLoad(false);
                    Toast.makeText(getContext(), "Unable to retrieve tweets.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    List<Tweet> loadStaticTimeline(long maxId) {
        return Tweet.get();
    }

    @Override
    void clearStaticTimeline() {
        Tweet.clear();
    }
}