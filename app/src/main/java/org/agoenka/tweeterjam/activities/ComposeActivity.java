package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.databinding.ActivityComposeBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;

public class ComposeActivity extends AppCompatActivity {

    private ActivityComposeBinding binding;
    private TwitterClient client;
    private Tweet replyToTweet;

    private static final int MAX_TWEET_LENGTH = 140;

    static int REQUEST_CODE_COMPOSE = 1;
    static int REQUEST_CODE_REPLY = 1;

    static final String LOGGED_IN_USER_KEY = "loggedInUser";
    static final String TWEET_KEY = "tweet";
    static final String IN_REPLY_TO_KEY = "inReplyTo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compose);
        binding.setHandlers(new Handlers());
        client = TweeterJamApplication.getTwitterClient();

        User user = Parcels.unwrap(getIntent().getParcelableExtra(LOGGED_IN_USER_KEY));
        binding.setUser(user);
        setTextChangeListener();

        replyToTweet = Parcels.unwrap(getIntent().getParcelableExtra(IN_REPLY_TO_KEY));
        if (replyToTweet != null) {
            binding.etTweet.setText(String.format("%s ", replyToTweet.getUser().getScreenName()));
            binding.etTweet.setSelection(binding.etTweet.getText().length());
            binding.tvInReplyTo.setText(String.format("In reply to %s", replyToTweet.getUser().getName()));
            binding.tvInReplyTo.setVisibility(View.VISIBLE);
        }
    }

    private void setTextChangeListener() {
        final int startColor = binding.tvCharCount.getCurrentTextColor();
        binding.tvCharCount.setText(String.format(Locale.getDefault(), "%d", MAX_TWEET_LENGTH));
        binding.etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                int len = MAX_TWEET_LENGTH - s.length();
                binding.tvCharCount.setText(String.format(Locale.getDefault(), "%d", len));
                if (len < 0) {
                    binding.tvCharCount.setTextColor(ContextCompat.getColor(ComposeActivity.this, R.color.colorAccent));
                    binding.btnTweet.setEnabled(false);
                } else if (!binding.btnTweet.isEnabled()) {
                    binding.tvCharCount.setTextColor(startColor);
                    binding.btnTweet.setEnabled(true);
                }
            }
        });
    }

    public class Handlers {
        public void onCancel(@SuppressWarnings("unused") View view) {
            Toast.makeText(ComposeActivity.this, "Tweet Canceled", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }

        public void onTweet(@SuppressWarnings("unused") View view) {
            if (replyToTweet != null)
                postTweet(binding.etTweet.getText().toString(), replyToTweet.getUid());
            else
                postTweet(binding.etTweet.getText().toString(), 0);
        }
    }

    private void postTweet(String status, long inReplyTo) {
        if (isConnected(this)) {
            client.postTweet(status, inReplyTo, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    Tweet tweet = Tweet.fromJSON(response);
                    Intent data = new Intent();
                    data.putExtra(TWEET_KEY, Parcels.wrap(tweet));
                    setResult(RESULT_OK, data);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(ComposeActivity.this, "Unable to post tweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}