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
import org.agoenka.tweeterjam.databinding.ActivityTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;

public class TweetActivity extends AppCompatActivity {

    private ActivityTweetBinding binding;
    private TwitterClient client;
    private Tweet tweet;

    static int REQUEST_CODE_COMPOSE = 1;

    static final String USER_KEY = "loggedInUser";
    static final String TWEET_KEY = "tweet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tweet);
        binding.setHandlers(new Handlers());
        client = TweeterJamApplication.getTwitterClient();

        User user = Parcels.unwrap(getIntent().getParcelableExtra(USER_KEY));
        binding.setUser(user);
    }

    public class Handlers {
        public void onCancel(@SuppressWarnings("unused") View view) {
            Toast.makeText(TweetActivity.this, "Tweet Canceled", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }

        public void onTweet(@SuppressWarnings("unused") View view) {
            postTweet(binding.etTweet.getText().toString());
        }
    }

    private void postTweet(String status) {
        if (isConnected(this)) {
            client.postTweet(status, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    tweet = Tweet.fromJSON(response);
                    Intent data = new Intent();
                    data.putExtra(TWEET_KEY, Parcels.wrap(tweet));
                    setResult(RESULT_OK, data);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(TweetActivity.this, "Unable to post tweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}