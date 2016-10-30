package org.agoenka.tweeterjam.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.agoenka.tweeterjam.TweeterJamDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */
@Parcel(analyze = User.class)
@Table(database = TweeterJamDatabase.class)
public class User {

    @Column @PrimaryKey long uid;
    @Column String name;
    @Column String screenName;
    @Column String profileImageUrl;

    @SuppressWarnings("unused")
    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public User() {}

    // deserialize the user json => user
    public static User fromJSON(JSONObject jsonObject) {
        User u = new User();
        // Extract and fill the values
        try {
            u.uid = jsonObject.getLong("id");
            u.name = jsonObject.getString("name");
            u.screenName = jsonObject.getString("screen_name");
            u.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Return a user
        return u;
    }

}