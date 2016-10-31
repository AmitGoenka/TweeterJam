package org.agoenka.tweeterjam.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityDetailBinding;
import org.agoenka.tweeterjam.fragments.ComposeTweetFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.fragments.ComposeTweetFragment.LOGGED_IN_USER_KEY;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private User loggedInUser;

    static final String TWEET_KEY = "tweet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.setHandlers(new Handlers());
        setSupportActionBar(binding.appbarMain.toolbar);

        loggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(LOGGED_IN_USER_KEY));
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra(TWEET_KEY));
        binding.setTweet(tweet);
    }

    public class Handlers {
        public void onReply(@SuppressWarnings("unused") View view) {
            ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance("Tweet", loggedInUser, binding.getTweet());
            composeDialog.setListener(tweet -> Toast.makeText(DetailActivity.this, "Replied Successfully!", Toast.LENGTH_SHORT).show());
            composeDialog.show(getSupportFragmentManager(), "Compose Tweet");
        }
    }
}
