package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.adapters.EndlessRecyclerViewScrollListener;
import org.agoenka.tweeterjam.adapters.ItemClickSupport;
import org.agoenka.tweeterjam.adapters.TweetsAdapter;
import org.agoenka.tweeterjam.databinding.ActivityTimelineBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.activities.ComposeActivity.LOGGED_IN_USER_KEY;
import static org.agoenka.tweeterjam.activities.ComposeActivity.REQUEST_CODE_COMPOSE;
import static org.agoenka.tweeterjam.activities.ComposeActivity.TWEET_KEY;
import static org.agoenka.tweeterjam.network.TwitterClient.PAGE_SIZE;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;

public class TimelineActivity extends AppCompatActivity {

    private ActivityTimelineBinding binding;
    private TwitterClient client;
    private User loggedInUser;
    private List<Tweet> mTweets;
    private TweetsAdapter mAdapter;
    private long currMinId = 0;
    private long currMaxId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        client = TweeterJamApplication.getTwitterClient();
        setupViews();

        if (!isConnected(this)) {
            mTweets.addAll(Tweet.get());
            mAdapter.notifyItemRangeInserted(0, mTweets.size());
        } else {
            Tweet.clear();
            getUserCredentials();
            populateTimeline(currMinId, currMaxId);
        }
    }

    private void setupViews() {
        mTweets = new ArrayList<>();
        mAdapter = new TweetsAdapter(this, mTweets);
        binding.rvTweets.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvTweets.setLayoutManager(layoutManager);

        binding.rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemCount, RecyclerView view) {
                currMinId = Tweet.getMinId(mTweets);
                populateTimeline(currMinId > 0 ? currMinId - 1 : 0, 0);
            }
        });

        ItemClickSupport.addTo(binding.rvTweets).setOnItemClickListener((recyclerView, position, v) -> {
            Tweet tweet = mTweets.get(position);
            Intent intent = new Intent(TimelineActivity.this, DetailActivity.class);
            intent.putExtra(TWEET_KEY, Parcels.wrap(tweet));
            intent.putExtra(LOGGED_IN_USER_KEY, Parcels.wrap(loggedInUser));
            startActivity(intent);
        });

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(() -> {
            // once the network request has completed successfully swipeContainer.setRefreshing(false) must be called.
            refreshTimeline();
            binding.swipeContainer.setRefreshing(false);
        });

        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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

        if (id == R.id.action_compose) {
            Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
            intent.putExtra(LOGGED_IN_USER_KEY, Parcels.wrap(loggedInUser));
            startActivityForResult(intent, REQUEST_CODE_COMPOSE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(TWEET_KEY));
            mTweets.add(0, tweet);
            mAdapter.notifyItemRangeInserted(0, 1);

            mTweets.remove(tweet);
            refreshTimeline();
        }
    }

    private void refreshTimeline() {
        currMaxId = Tweet.getMaxId(mTweets);
        populateTimeline(0, currMaxId);
    }

    // Send an API request to get the timeline json
    // Fill the list view by creating the tweet objects from json
    private void populateTimeline(final long maxId, final long sinceId) {
        if (isConnected(this)) {
            client.getHomeTimeline(PAGE_SIZE, maxId, sinceId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    Log.d("DEBUG", json.toString());
                    List<Tweet> tweets = Tweet.fromJSONArray(json);
                    if (sinceId > 0) {
                        mTweets.addAll(0, tweets);
                        mAdapter.notifyItemRangeInserted(0, tweets.size());
                    } else {
                        int currentSize = mAdapter.getItemCount();
                        mTweets.addAll(tweets);
                        mAdapter.notifyItemRangeInserted(currentSize, mTweets.size() - currentSize);
                    }
                    Tweet.save(tweets);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(TimelineActivity.this, "Unable to retrieve tweets.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Fetch the logged in user's credentials
    private void getUserCredentials() {
        if (isConnected(this)) {
            client.getUserCredentials(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Log.d("DEBUG", json.toString());
                    loggedInUser = User.fromJSON(json);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(loggedInUser.getScreenName());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(TimelineActivity.this, "Unable to retrieve account information.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}