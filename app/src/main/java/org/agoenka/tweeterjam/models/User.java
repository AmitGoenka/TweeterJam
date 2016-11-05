package org.agoenka.tweeterjam.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.agoenka.tweeterjam.TweeterJamDatabase;
import org.parceler.Parcel;

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

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public String getProfileImageUrl() {
        if (!TextUtils.isEmpty(profileImageUrl) && profileImageUrl.contains("_normal"))
            return profileImageUrl.replace("_normal", "_bigger");
        return profileImageUrl;
    }

    public long getUser() {
        return uid;
    }

    public User() {}

}