package org.agoenka.tweeterjam.models;

import android.os.Build;
import android.text.Html;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.agoenka.tweeterjam.TweeterJamDatabase;
import org.agoenka.tweeterjam.utils.DateUtils;
import org.parceler.Parcel;

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

    @SerializedName("id") @Column @PrimaryKey long uid;
    @Column String createdAt;
    @SerializedName("text") @Column String body;
    @Column int retweetCount;
    @Column int favoriteCount;
    @Column boolean retweeted;
    @Column boolean favorited;
    @SerializedName("entities") @Column @ForeignKey Entity entity;
    @SerializedName("extended_entities")@Column @ForeignKey ExtendedEntity extendedEntity;
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

    public Entity getEntity() {
        return entity;
    }

    @SuppressWarnings("unused")
    public ExtendedEntity getExtendedEntity() {
        return extendedEntity;
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
            if (tweet.entity != null) {
                tweet.entity.save();
            }
            if (tweet.extendedEntity != null) {
                tweet.extendedEntity.save();
            }
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
        Delete.tables(Tweet.class, User.class, Entity.class, ExtendedEntity.class);
    }

}