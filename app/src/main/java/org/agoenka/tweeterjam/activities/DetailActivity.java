package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.databinding.ActivityDetailBinding;
import org.agoenka.tweeterjam.fragments.ComposeTweetFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;
import static org.agoenka.tweeterjam.fragments.ComposeTweetFragment.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private TwitterClient client;
    private User loggedInUser;

    static final String KEY_TWEET = "tweet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.setHandlers(new Handlers());
        setSupportActionBar(binding.appbarMain.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = TweeterJamApplication.getTwitterClient();
        loggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(KEY_LOGGED_IN_USER));
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra(KEY_TWEET));
        binding.setTweet(tweet);
    }

    public class Handlers {
        public void onReply(@SuppressWarnings("unused") View view) {
            ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(null, loggedInUser, binding.getTweet());
            composeDialog.setListener(tweet -> Toast.makeText(DetailActivity.this, "Replied Successfully!", Toast.LENGTH_SHORT).show());
            composeDialog.show(getSupportFragmentManager(), "Compose Tweet");
        }

        public void onRetweet(@SuppressWarnings("unused") View view) {
            if (!binding.getTweet().isRetweeted()) {
                retweet(binding.getTweet().getUid());
            }
        }

        public void onFavorite(@SuppressWarnings("unused") View view) {
            if (!binding.getTweet().isFavorited()) {
                favorite(binding.getTweet().getUid());
            }
        }

        public void onShare(@SuppressWarnings("unused") View view) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, binding.getTweet().getBody());
            startActivity(Intent.createChooser(shareIntent, "Share using"));
        }
    }

    private void retweet(long uid) {
        if (isConnected(getContext())) {
            client.postRetweet(uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    binding.getTweet().setRetweeted(true);
                    Toast.makeText(DetailActivity.this, "Retweet successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(DetailActivity.this, "Unable to retweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void favorite(long uid) {
        if (isConnected(getContext())) {
            client.createFavorite(uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    binding.getTweet().setFavorited(true);
                    Toast.makeText(DetailActivity.this, "Favorite created", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(DetailActivity.this, "Unable to favorite at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}