package org.agoenka.tweeterjam.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.agoenka.tweeterjam.TweeterJamDatabase;
import org.parceler.Parcel;

import java.util.Locale;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */
@Parcel(analyze = User.class)
@Table(database = TweeterJamDatabase.class)
public class User extends BaseModel {

    @SerializedName("id") @Column @PrimaryKey long uid;
    @Column String name;
    @Column String screenName;
    @Column String profileImageUrl;
    @SerializedName("description") @Column String tagline;
    @Column int followersCount;
    @Column int friendsCount;

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public String getProfileImageUrl() {
        return !TextUtils.isEmpty(profileImageUrl) && profileImageUrl.contains("_normal")
                ? profileImageUrl.replace("_normal", "_bigger")
                : profileImageUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public String getFollowersCount() {
        return String.format(Locale.getDefault(), "%d", followersCount);
    }

    public String getFriendsCount() {
        return String.format(Locale.getDefault(), "%d", friendsCount);
    }

    public long getUser() {
        return uid;
    }

    public User() {}

}