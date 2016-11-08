package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ActivityMessageBinding;
import org.agoenka.tweeterjam.fragments.MessageListFragment;
import org.agoenka.tweeterjam.fragments.SendMessageFragment;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.TAG_FRAGMENT_MESSAGELIST;

public class MessageActivity extends AppCompatActivity {

    private User mLoggedInUser;
    private MenuItem miActionProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
        if (savedInstanceState == null) {
            loadMessagesFragment();
        }
    }

    private void bindViews() {
        mLoggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(KEY_LOGGED_IN_USER));
        ActivityMessageBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_message);
        setSupportActionBar(binding.appbarMain.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadMessagesFragment() {
        MessageListFragment messagesFragment = MessageListFragment
                .newInstance()
                .setMessageSendListener(this::sendMessage)
                .setProfileListener(this::onProfileSelected)
                .setOnLoadingListener(this::onLoading);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, messagesFragment, TAG_FRAGMENT_MESSAGELIST)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgress = menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }

    public void onMessage(MenuItem item) {
        sendMessage(null);
    }

    private void sendMessage(User user) {
        String screenName = user != null ? user.getScreenName() : null;
        SendMessageFragment messageDialog = SendMessageFragment.newInstance(screenName);
        messageDialog.setListener(message -> {
            MessageListFragment fragment = (MessageListFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_MESSAGELIST);
            if (fragment != null && fragment.isAdded() && !fragment.isRemoving()) {
                miActionProgress.setVisible(true);
                fragment.add(0, message);
            }
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
        });
        messageDialog.show(getSupportFragmentManager(), "Send Message");
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