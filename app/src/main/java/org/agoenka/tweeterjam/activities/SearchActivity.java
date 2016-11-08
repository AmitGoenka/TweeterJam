package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivitySearchBinding;
import org.agoenka.tweeterjam.fragments.ComposeTweetFragment;
import org.agoenka.tweeterjam.fragments.SearchTimelineFragment;
import org.agoenka.tweeterjam.fragments.TweetsListFragment;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static android.support.v4.view.MenuItemCompat.getActionView;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_QUERY;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_TWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.TAG_FRAGMENT_SEARCH;

public class SearchActivity extends AppCompatActivity implements
        TweetsListFragment.OnItemSelectedListener,
        TweetsListFragment.OnProfileSelectedListener,
        TweetsListFragment.OnReplyListener,
        TweetsListFragment.OnLoadingListener {

    private User loggedInUser;
    private String mQuery;
    private MenuItem miActionProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(KEY_LOGGED_IN_USER));
        mQuery = getIntent().getStringExtra(KEY_QUERY);
        bindViews();

        if (savedInstanceState == null) {
            loadSearchTimeline();
        }
    }

    private void bindViews() {
        ActivitySearchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        setSupportActionBar(binding.appbarMain.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(String.format(getString(R.string.search_results_for), mQuery));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadSearchTimeline() {
        SearchTimelineFragment timelineFragment = SearchTimelineFragment.newInstance(mQuery);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flTimelineContainer, timelineFragment, TAG_FRAGMENT_SEARCH)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem menuItem = menu.findItem(R.id.miActionSearch);
        final SearchView searchView = (SearchView) getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query;
                SearchTimelineFragment fragment = (SearchTimelineFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SEARCH);
                if (fragment != null && fragment.isAdded() && !fragment.isRemoving()) {
                    miActionProgress.setVisible(true);
                    fragment.search(mQuery);
                }
                searchView.clearFocus();
                menuItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgress = menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
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
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(KEY_LOGGED_IN_USER, Parcels.wrap(loggedInUser));
        intent.putExtra(KEY_USER, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    public void onReply(Tweet tweet) {
        ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(null, loggedInUser, tweet);
        composeDialog.setListener(newTweet -> Toast.makeText(this, "Replied Successfully!", Toast.LENGTH_SHORT).show());
        composeDialog.show(getSupportFragmentManager(), "Compose Tweet");
    }

    @Override
    public void onLoad(boolean loading) {
        if (miActionProgress != null) miActionProgress.setVisible(loading);
    }
}