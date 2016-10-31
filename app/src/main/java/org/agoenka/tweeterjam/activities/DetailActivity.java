package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityDetailBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.activities.ComposeActivity.LOGGED_IN_USER_KEY;
import static org.agoenka.tweeterjam.activities.ComposeActivity.IN_REPLY_TO_KEY;
import static org.agoenka.tweeterjam.activities.ComposeActivity.REQUEST_CODE_REPLY;
import static org.agoenka.tweeterjam.activities.ComposeActivity.TWEET_KEY;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.setHandlers(new Handlers());

        loggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(LOGGED_IN_USER_KEY));
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra(TWEET_KEY));
        binding.setTweet(tweet);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class Handlers {
        public void onReply(@SuppressWarnings("unused") View view) {
            Intent intent = new Intent(DetailActivity.this, ComposeActivity.class);
            intent.putExtra(LOGGED_IN_USER_KEY, Parcels.wrap(loggedInUser));
            intent.putExtra(IN_REPLY_TO_KEY, Parcels.wrap(binding.getTweet()));
            startActivityForResult(intent, REQUEST_CODE_REPLY);
        }
    }
}
