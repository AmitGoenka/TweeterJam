package org.agoenka.tweeterjam.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.adapters.EndlessScrollListener;
import org.agoenka.tweeterjam.adapters.TweetsArrayAdapter;
import org.agoenka.tweeterjam.databinding.ActivityTimelineBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;

public class TimelineActivity extends AppCompatActivity {

    private ActivityTimelineBinding binding;
    private TwitterClient client;
    private List<Tweet> mTweets;
    private TweetsArrayAdapter mAdapter;

    private int count = 25;
    private long minId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        setupViews();
        client = TweeterJamApplication.getTwitterClient(); // singleton client
        populateTimeline(minId, 0);
    }

    private void setupViews() {
        // Create the arraylist (data source)
        mTweets = new ArrayList<>();
        // Construct the mAdapter from data source
        mAdapter = new TweetsArrayAdapter(this, mTweets);
        // Connect mAdapter to list view
        binding.lvTweets.setAdapter(mAdapter);

        binding.lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemCount) {
                long id = Tweet.getMinId(mTweets);
                if (id > 0) minId = id;
                populateTimeline(minId - 1, 0);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if present
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar will automatically handle clicks on the Home/Up button,
        // so long the parent activity is specified in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Send an API request to get the timeline json
    // Fill the list view by creating the tweet objects from json
    private void populateTimeline(long maxId, long sinceId) {
        if (isConnected(this)) {
            client.getHomeTimeline(count, maxId, sinceId, new JsonHttpResponseHandler() {
                // Success
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    Log.d("DEBUG", json.toString());
                    mAdapter.addAll(Tweet.fromJSONArray(json));
                    Log.d("DEBUG", mAdapter.toString());
                }

                // Failure
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(TimelineActivity.this, "Error occurred while retrieving tweets.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
