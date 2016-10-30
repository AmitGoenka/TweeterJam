package org.agoenka.tweeterjam.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityDetailBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.activities.TimelineActivity.TWEET_KEY;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.setHandlers(new Handlers());

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra(TWEET_KEY));
        binding.setTweet(tweet);
    }

    public class Handlers {
        public void onReply(@SuppressWarnings("unused") View view) {

        }
    }
}
