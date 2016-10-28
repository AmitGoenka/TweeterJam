package org.agoenka.tweeterjam;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.agoenka.tweeterjam.network.TwitterClient;

/**
 * Author: agoenka
 * Created At: 10/27/2016
 * Version: ${VERSION}
 */

public class TweeterJamApplication extends Application {

    private static final boolean DEBUG = false;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        TweeterJamApplication.context = this;

        FlowManager.init(new FlowConfig.Builder(this).build());
        if(DEBUG) FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
    }

    public static TwitterClient getTwitterClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TweeterJamApplication.context);
    }

}