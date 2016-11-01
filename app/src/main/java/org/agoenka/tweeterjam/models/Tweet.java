package org.agoenka.tweeterjam.models;

import android.os.Build;
import android.text.Html;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.agoenka.tweeterjam.TweeterJamDatabase;
import org.agoenka.tweeterjam.utils.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.agoenka.tweeterjam.utils.DateUtils.TWITTER_FORMAT;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */
@Parcel(analyze = Tweet.class)
@Table(database = TweeterJamDatabase.class)
public class Tweet extends BaseModel {

    @Column @PrimaryKey long uid;
    @Column String createdAt;
    @Column String body;
    @Column int retweetCount;
    @Column int favoriteCount;
    @Column boolean retweeted;
    @Column boolean favorited;
    @Column String imageUrl;
    @Column String videoContentType;
    @Column String videoUrl;
    @Column @ForeignKey(saveForeignKeyModel = true) User user;

    public long getUid() {
        return uid;
    }

    private String getCreatedAt() {
        return createdAt;
    }

    public String getBody() {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            return Html.fromHtml(body).toString();
        }
    }

    public String getRetweetCount() {
        return String.format(Locale.getDefault(), "%d", retweetCount);
    }

    public String getFavoriteCount() {
        return String.format(Locale.getDefault(), "%d", favoriteCount);
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @SuppressWarnings("unused")
    public String getVideoContentType() {
        return videoContentType;
    }

    @SuppressWarnings("unused")
    public String getVideoUrl() {
        return videoUrl;
    }

    public User getUser() {
        return user;
    }

    public String getDuration() {
        return DateUtils.getDuration(this.getCreatedAt(), TWITTER_FORMAT);
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public Tweet() {}

    // Tweet.fromJSON("{ ... }") => <Tweet>
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        //Extract the values from the json, store them
        try {
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.body = jsonObject.getString("text");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

            setImageData(jsonObject, tweet);
            setVideoData(jsonObject, tweet);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Return the tweet object
        return tweet;
    }

    private static void setImageData(JSONObject jsonObject, Tweet tweet) {
        try {
            JSONArray entitiesMedia = jsonObject.getJSONObject("entities").getJSONArray("media");
            if (entitiesMedia != null) {
                for (int i = 0; i < entitiesMedia.length(); i++) {
                    String type = entitiesMedia.getJSONObject(i).getString("type");
                    if ("photo".equalsIgnoreCase(type)) {
                        tweet.imageUrl = entitiesMedia.getJSONObject(i).getString("media_url");
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setVideoData(JSONObject jsonObject, Tweet tweet) {
        try {
            JSONArray extendedEntitiesMedia = jsonObject.getJSONObject("extended_entities").getJSONArray("media");
            if (extendedEntitiesMedia != null) {
                for (int i = 0; i < extendedEntitiesMedia.length(); i++) {
                    String type = extendedEntitiesMedia.getJSONObject(i).getString("type");
                    if ("video".equalsIgnoreCase(type)) {
                        JSONObject videoInfo = extendedEntitiesMedia.getJSONObject(i).getJSONObject("video_info");
                        if (videoInfo != null) {
                            JSONArray variants = videoInfo.getJSONArray("variants");
                            if (variants != null) {
                                tweet.videoContentType = variants.getJSONObject(0).getString("content_type");
                                tweet.videoUrl = variants.getJSONObject(0).getString("url");
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public static long getMinId(List<Tweet> tweets) {
        long min = 0;
        for (Tweet tweet : tweets) {
            if (tweet.getUid() < min || min == 0) {
                min = tweet.getUid();
            }
        }
        if (tweets.size() > 0 && min != tweets.get(tweets.size() - 1).getUid()) {
            Log.d("DEBUG", "The identified min id is not at the bottom of list!");
        }
        return min;
    }

    public static List<Tweet> get() {
        return SQLite.select().from(Tweet.class).orderBy(Tweet_Table.createdAt, false).limit(200).queryList();
    }

    private static void save(Tweet tweet) {
        if (tweet != null) {
            if (tweet.user != null) {
                tweet.user.save();
            }
            tweet.save();
        }
    }

    public static void save(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            save(tweet);
        }
    }

    public static void clear() {
        Delete.tables(Tweet.class, User.class);
    }

}