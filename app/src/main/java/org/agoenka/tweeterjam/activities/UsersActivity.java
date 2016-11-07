package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityUsersBinding;
import org.agoenka.tweeterjam.fragments.UsersListFragment;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_MODE;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;

public class UsersActivity extends AppCompatActivity {

    private User mUser;
    private User mLoggedInUser;
    private String mode;
    private MenuItem miActionProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
        if (savedInstanceState == null) {
            loadUsersFragment();
        }
    }

    private void bindViews() {
        mUser = Parcels.unwrap(getIntent().getParcelableExtra(KEY_USER));
        mLoggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(KEY_LOGGED_IN_USER));
        mode = getIntent().getStringExtra(KEY_MODE);

        ActivityUsersBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_users);
        setSupportActionBar(binding.appbarMain.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mUser.getScreenName());
    }

    private void loadUsersFragment() {
        UsersListFragment usersFragment = UsersListFragment
                .newInstance(mUser, mode)
                .setProfileListener(this::onProfileSelected)
                .setOnLoadingListener(this::onLoading);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, usersFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgress = menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }

    private void onProfileSelected(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(KEY_LOGGED_IN_USER, Parcels.wrap(mLoggedInUser));
        intent.putExtra(KEY_USER, Parcels.wrap(user));
        startActivity(intent);
    }

    private void onLoading(boolean loading) {
        if (miActionProgress != null) miActionProgress.setVisible(loading);
    }
}