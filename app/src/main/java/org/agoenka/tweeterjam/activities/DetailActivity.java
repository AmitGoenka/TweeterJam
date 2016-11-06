package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityDetailBinding;
import org.agoenka.tweeterjam.fragments.DetailFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_TWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.getPermissionIntent;
import static org.agoenka.tweeterjam.utils.AppUtils.hasWritePermission;
import static org.agoenka.tweeterjam.utils.AppUtils.missingWritePermission;

public class DetailActivity extends AppCompatActivity {

    private Tweet mTweet;
    private User mLoggedInUser;

    private Intent startingIntent;
    boolean isPermissionSet = false;
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
                isPermissionSet = true;
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
        DetailFragment detailFragment = DetailFragment.newInstance(mTweet, mLoggedInUser);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, detailFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_SETTINGS_PERMISSION && hasWritePermission(this)) {
            isPermissionSet = true;
            finish();
            startActivity(startingIntent);
        }
    }
}