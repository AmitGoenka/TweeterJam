package org.agoenka.tweeterjam.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.models.Statuses;
import org.agoenka.tweeterjam.models.Tweet;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.network.TwitterClient.PAGE_SIZE;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_QUERY;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;

/**
 * Author: agoenka
 * Created At: 11/7/2016
 * Version: ${VERSION}
 */

public class SearchTimelineFragment extends TweetsListFragment {

    public static SearchTimelineFragment newInstance(String query) {
        SearchTimelineFragment userFragment = new SearchTimelineFragment();
        Bundle args = new Bundle();
        args.putString(KEY_QUERY, query);
        userFragment.setArguments(args);
        return userFragment;
    }

    public void search(String query) {
        getArguments().putString(KEY_QUERY, query);
        populateTimeline(0, true);
    }


    @Override
    void populateTimeline(long maxId, boolean refresh) {
        String query = getArguments().getString(KEY_QUERY);
        if (isConnected(getContext())) {
            client.searchTweets(query, maxId, PAGE_SIZE, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String json) {
                    Log.d("DEBUG", json);
                    Statuses statuses = getGson().fromJson(json, Statuses.class);
                    if (statuses != null && statuses.getTweets() != null) {
                        addAll(statuses.getTweets(), refresh);
                    }
                    if (loadingListener != null) loadingListener.onLoad(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("DEBUG", responseString);
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    if (loadingListener != null) loadingListener.onLoad(false);
                    Toast.makeText(getContext(), "Unable to search tweets at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    List<Tweet> loadStaticTimeline(long maxId) {
        return null;
    }

    @Override
    void clearStaticTimeline() {
        // Do nothing
    }
}