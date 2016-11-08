package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityDetailBinding;
import org.agoenka.tweeterjam.fragments.ComposeTweetFragment;
import org.agoenka.tweeterjam.fragments.DetailFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_TWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.getPermissionIntent;
import static org.agoenka.tweeterjam.utils.AppUtils.hasWritePermission;
import static org.agoenka.tweeterjam.utils.AppUtils.missingWritePermission;

public class DetailActivity extends AppCompatActivity
        implements DetailFragment.OnProfileSelectedListener, DetailFragment.OnReplyListener, DetailFragment.OnShareListener {

    private Tweet mTweet;
    private User mLoggedInUser;

    private Intent startingIntent;
    private static final int WRITE_SETTINGS_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTweet = Parcels.unwrap(getIntent().getParcelableExtra(KEY_TWEET));
        mLoggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(KEY_LOGGED_IN_USER));

        if (mTweet.hasVideo()) {
            startingIntent = getIntent();
            if (missingWritePermission(this)) {
                startActivityForResult(getPermissionIntent(this), WRITE_SETTINGS_PERMISSION);
            } else {
                bindViews();
            }
        } else {
            bindViews();
        }

        if (savedInstanceState == null) {
            loadDetailFragment();
        }
    }

    private void bindViews() {
        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        setSupportActionBar(binding.appbarMain.toolbar);
    }

    private void loadDetailFragment() {
        DetailFragment detailFragment = DetailFragment.newInstance(mTweet);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, detailFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_SETTINGS_PERMISSION && hasWritePermission(this)) {
            finish();
            startActivity(startingIntent);
        }
    }

    @Override
    public void onProfileSelected(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(KEY_LOGGED_IN_USER, Parcels.wrap(mLoggedInUser));
        intent.putExtra(KEY_USER, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    public void onReply(Tweet tweet) {
        ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(null, mLoggedInUser, tweet);
        composeDialog.setListener(aTweet -> Toast.makeText(this, "Replied Successfully!", Toast.LENGTH_SHORT).show());
        composeDialog.show(getSupportFragmentManager(), "Compose Tweet");
    }

    @Override
    public void onShare(Tweet tweet) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, tweet.getBody());
        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }
}