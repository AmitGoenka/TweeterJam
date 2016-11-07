package org.agoenka.tweeterjam.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Author: agoenka
 * Created At: 11/7/2016
 * Version: ${VERSION}
 */
@Parcel(analyze = Statuses.class)
public class Statuses {

    @SerializedName("statuses") List<Tweet> tweets;

    public List<Tweet> getTweets() {
        return tweets;
    }

    public Statuses() {}
}