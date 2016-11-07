package org.agoenka.tweeterjam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.network.TwitterClient;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // Callback to this method implies that OAuth was authenticated successfully.
    // This method launches the primary authenticated activity
    @Override
    public void onLoginSuccess() {
        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
    }

    // Callback to this method implies that OAuth authentication flow failed.
    // Handle the error through error dialog or toasts
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    // Click handler method that starts the OAuth flow based on user action (such as click on login button)
    // This function uses the Twitter client to initiate OAuth authorization
    public void loginTwitter(View view) {
        getClient().connect();
    }

}