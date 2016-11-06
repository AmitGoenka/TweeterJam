package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityProfileBinding;
import org.agoenka.tweeterjam.fragments.TweetsListFragment;
import org.agoenka.tweeterjam.fragments.ProfileFragment;
import org.agoenka.tweeterjam.fragments.UserTimelineFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_TWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;

public class ProfileActivity extends AppCompatActivity implements TweetsListFragment.OnProfileSelectedListener, TweetsListFragment.OnItemSelectedListener {

    private User loggedInUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(KEY_LOGGED_IN_USER));
        user = Parcels.unwrap(getIntent().getParcelableExtra(KEY_USER));
        bindViews();

        if (savedInstanceState == null) {
            loadUserProfile();
            loadUserTimeline();
        }
    }

    private void bindViews() {
        ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        if (user == null)
            user = loggedInUser;

        setSupportActionBar(binding.appbarMain.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(user.getScreenName());
    }

    private void loadUserProfile() {
        // Create the user timeline fragment
        ProfileFragment profileFragment = ProfileFragment.newInstance(user);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flProfileContainer, profileFragment)
                .commit();
    }

    private void loadUserTimeline() {
        // Create the user timeline fragment
        UserTimelineFragment timelineFragment = UserTimelineFragment.newInstance(user.getScreenName());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flTimelineContainer, timelineFragment)
                .commit();
    }

    @Override
    public void onItemSelected(Tweet tweet) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(KEY_TWEET, Parcels.wrap(tweet));
        intent.putExtra(KEY_LOGGED_IN_USER, Parcels.wrap(loggedInUser));
        startActivity(intent);
    }

    @Override
    public void onProfileSelected(User user) {
        // Do Nothing since the user does not need to navigate to the same profile again.
    }
}