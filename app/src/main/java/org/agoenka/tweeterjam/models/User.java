package org.agoenka.tweeterjam.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */

public class User {
    // list attributes
    private long uid;
    private String name;
    private String screenName;
    private String profileImageUrl;

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
