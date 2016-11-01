package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.adapters.EndlessRecyclerViewScrollListener;
import org.agoenka.tweeterjam.adapters.ItemClickSupport;
import org.agoenka.tweeterjam.adapters.TweetsAdapter;
import org.agoenka.tweeterjam.databinding.ActivityTimelineBinding;
import org.agoenka.tweeterjam.fragments.ComposeTweetFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.activities.DetailActivity.KEY_TWEET;
import static org.agoenka.tweeterjam.fragments.ComposeTweetFragment.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.network.TwitterClient.PAGE_SIZE;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;

public class TimelineActivity extends AppCompatActivity {

    private ActivityTimelineBinding binding;
    private TwitterClient client;
    private User loggedInUser;
    private List<Tweet> mTweets;
    private TweetsAdapter mAdapter;
    private long currMinId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        binding.setHandlers(new Handlers());
        setSupportActionBar(binding.appbarMain.toolbar);
        client = TweeterJamApplication.getTwitterClient();

        getUserCredentials();
        setupViews();

        if (!isConnected(this)) {
            mAdapter.addAll(Tweet.get(), true);
        } else {
            Tweet.clear();
            populateTimeline(currMinId, true);
        }
    }

    private void handleImplicitIntents() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String url = intent.getStringExtra(Intent.EXTRA_TEXT);
                ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(getTweetText(title, url), loggedInUser, null);
                composeDialog.setListener(tweet -> mAdapter.add(0, tweet));
                composeDialog.show(getSupportFragmentManager(), "Compose Tweet");
            }
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
                populateTimeline(currMinId > 0 ? currMinId - 1 : 0, false);
            }
        });

        ItemClickSupport.addTo(binding.rvTweets).setOnItemClickListener((recyclerView, position, v) -> {
            Tweet tweet = mTweets.get(position);
            Intent intent = new Intent(TimelineActivity.this, DetailActivity.class);
            intent.putExtra(KEY_TWEET, Parcels.wrap(tweet));
            intent.putExtra(KEY_LOGGED_IN_USER, Parcels.wrap(loggedInUser));
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

    public class Handlers {
        public void onCompose(@SuppressWarnings("unused") View view) {
            ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(null, loggedInUser, null);
            composeDialog.setListener(tweet -> mAdapter.add(0, tweet));
            composeDialog.show(getSupportFragmentManager(), "Compose Tweet");
        }
    }

    private String getTweetText(String title, String url) {
        StringBuilder sb = new StringBuilder();
        if (title != null) {
            sb.append(title);
        }
        if (url != null) {
            if (title != null) {
                sb.append("\n").append(url);
            } else {
                sb.append(url);
            }
        }
        return sb.toString();
    }

    private void refreshTimeline() {
        mTweets.clear();
        populateTimeline(0, true);
    }

    // Send an API request to get the timeline json
    // Fill the list view by creating the tweet objects from json
    private void populateTimeline(final long maxId, final boolean refresh) {
        if (isConnected(this)) {
            client.getHomeTimeline(PAGE_SIZE, maxId, 0, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    Log.d("DEBUG", json.toString());
                    List<Tweet> tweets = Tweet.fromJSONArray(json);
                    mAdapter.addAll(tweets, refresh);
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
                    handleImplicitIntents();
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