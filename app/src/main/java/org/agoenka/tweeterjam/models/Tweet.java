package org.agoenka.tweeterjam.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */
public class Tweet {
    private long uid; // unique tweet id
    private String createdAt;
    private String body;
    private User user; // store embedded user object

    @SuppressWarnings("unused")
    public long getUid() {
        return uid;
    }

    @SuppressWarnings("unused")
    public String getCreatedAt() {
        return createdAt;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    // Deserialize the JSON and build Tweet objects
    // Tweet.fromJSON("{ ... }") => <Tweet>
    private static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        //Extract the values from the json, store them
        try {
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.body = jsonObject.getString("text");
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