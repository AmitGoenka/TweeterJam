package org.agoenka.tweeterjam.models;

import org.agoenka.tweeterjam.utils.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.agoenka.tweeterjam.utils.DateUtils.*;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */
public class Tweet {
    private long uid; // unique tweet id
    private String body;
    private int retweetCount;
    private String createdAt;
    private User user; // store embedded user object

    @SuppressWarnings("unused")
    public long getUid() {
        return uid;
    }

    public String getBody() {
        return body;
    }

    @SuppressWarnings("unused")
    public int getRetweetCount() {
        return retweetCount;
    }

    @SuppressWarnings("unused")
    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getDuration() {
        return DateUtils.getDuration(this.createdAt, TWITTER_FORMAT);
    }

    // Deserialize the JSON and build Tweet objects
    // Tweet.fromJSON("{ ... }") => <Tweet>
    private static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        //Extract the values from the json, store them
        try {
            tweet.uid = jsonObject.getLong("id");
            tweet.body = jsonObject.getString("text");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Return the tweet object
        return tweet;
    }

    // Tweet.fromJSONArray([ { ... }, { ... } ] => List<Tweet>)
    public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();
        // Iterate the json array and create tweets
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Return the finished list
        return tweets;
    }

}