package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityProfileBinding;
import org.agoenka.tweeterjam.fragments.TweetsListFragment;
import org.agoenka.tweeterjam.fragments.UserTimelineFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_TWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;

public class ProfileActivity extends AppCompatActivity implements TweetsListFragment.OnItemSelectedListener {

    private User loggedInUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(KEY_LOGGED_IN_USER));
        user = Parcels.unwrap(getIntent().getParcelableExtra(KEY_USER));
        if (user == null)
            user = loggedInUser;

        ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.setUser(user);

        setSupportActionBar(binding.appbarMain.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(user.getScreenName());

        if (savedInstanceState == null) {
            // Create the user timeline fragment
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(user.getScreenName());
            // Display user fragment within this activity(dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();// changes the fragments
        }
    }

    @Override
    public void onItemSelected(Tweet tweet) {
        Intent intent = new Intent(ProfileActivity.this, DetailActivity.class);
        intent.putExtra(KEY_TWEET, Parcels.wrap(tweet));
        intent.putExtra(KEY_LOGGED_IN_USER, Parcels.wrap(loggedInUser));
        startActivity(intent);
    }
}