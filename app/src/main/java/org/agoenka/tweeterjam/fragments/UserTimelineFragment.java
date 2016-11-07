package org.agoenka.tweeterjam.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.models.Tweet;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.network.TwitterClient.PAGE_SIZE;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_SCREEN_NAME;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;

/**
 * Author: agoenka
 * Created At: 11/6/2016
 * Version: ${VERSION}
 */

public class UserTimelineFragment extends TweetsListFragment {

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SCREEN_NAME, screenName);
        userFragment.setArguments(args);
        return userFragment;
    }

    @Override
    public void populateTimeline(final long maxId, final boolean refresh) {
        String screenName = getArguments().getString(KEY_SCREEN_NAME);
        if (isConnected(getContext())) {
            client.getUserTimeline(screenName, maxId, PAGE_SIZE, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String json) {
                    Log.d("DEBUG", json);
                    List<Tweet> tweets = getGson().fromJson(json, new TypeToken<List<Tweet>>() {}.getType());
                    addAll(tweets, refresh);
                    Tweet.save(tweets);
                    if (loadingListener != null) loadingListener.onLoad(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("DEBUG", responseString);
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    if (loadingListener != null) loadingListener.onLoad(false);
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