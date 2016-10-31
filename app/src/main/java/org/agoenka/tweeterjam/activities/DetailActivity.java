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

import static org.agoenka.tweeterjam.fragments.ComposeTweetFragment.KEY_LOGGED_IN_USER;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
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
    }
}
