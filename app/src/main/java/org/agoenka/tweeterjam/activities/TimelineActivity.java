package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.adapters.TweetsPagerAdapter;
import org.agoenka.tweeterjam.databinding.ActivityTimelineBinding;
import org.agoenka.tweeterjam.fragments.ComposeTweetFragment;
import org.agoenka.tweeterjam.fragments.TweetsListFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.activities.DetailActivity.KEY_TWEET;
import static org.agoenka.tweeterjam.fragments.ComposeTweetFragment.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.models.Tweet.getTweetText;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.OnItemSelectedListener {

    private ActivityTimelineBinding binding;
    private TwitterClient client;
    private TweetsPagerAdapter pagerAdapter;
    public User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        binding.setHandlers(new Handlers());
        client = TweeterJamApplication.getTwitterClient();

        setupViews();
        getUserCredentials();
    }

    private void setupViews() {
        setSupportActionBar(binding.appbarMain.toolbar);
        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        // Set the viewpager adapter for the pager
        binding.vpTimeline.setAdapter(pagerAdapter);
        // Attach the tabs to the pager
        binding.appbarMain.tlTimeline.setupWithViewPager(binding.vpTimeline);
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
                compose(getTweetText(title, url), null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem item) {
        // Launch the profile view
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(Tweet tweet) {
        Intent intent = new Intent(TimelineActivity.this, DetailActivity.class);
        intent.putExtra(KEY_TWEET, Parcels.wrap(tweet));
        intent.putExtra(KEY_LOGGED_IN_USER, Parcels.wrap(loggedInUser));
        startActivity(intent);
    }

    public class Handlers {
        public void onCompose(@SuppressWarnings("unused") View view) {
            compose(null, null);
        }
    }

    private void compose(String text, Tweet inReplyTo) {
        ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(text, loggedInUser, inReplyTo);
        composeDialog.setListener(tweet -> {
            Fragment fragment = pagerAdapter.getRegisteredFragment(0);
            if (fragment != null) {
                ((TweetsListFragment) fragment).add(0, tweet);
                binding.vpTimeline.setCurrentItem(0, true);
            }
        });
        composeDialog.show(getSupportFragmentManager(), "Compose Tweet");
    }

    // Fetch the logged in user's credentials
    private void getUserCredentials() {
        if (isConnected(this)) {
            client.getUserCredentials(new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("DEBUG", responseString);
                    loggedInUser = getGson().fromJson(responseString, User.class);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(loggedInUser.getScreenName());
                    }
                    handleImplicitIntents();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("DEBUG", responseString);
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(TimelineActivity.this, "Unable to retrieve account information.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}