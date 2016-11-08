package org.agoenka.tweeterjam.models;

import android.os.Build;
import android.text.Html;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.agoenka.tweeterjam.utils.DateUtils;
import org.parceler.Parcel;

import java.util.List;

import static org.agoenka.tweeterjam.utils.DateUtils.TWITTER_FORMAT;

/**
 * Author: agoenka
 * Created At: 11/7/2016
 * Version: ${VERSION}
 */
@Parcel(analyze = Message.class)
public class Message {

    @SerializedName("id") long uid;
    String text;
    User sender;
    String createdAt;

    public String getText() {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            return Html.fromHtml(text).toString();
        }
    }

    private long getUid() {
        return uid;
    }

    private String getCreatedAt() {
        return createdAt;
    }

    public User getSender() {
        return sender;
    }

    public String getDuration() {
        return DateUtils.getDuration(this.getCreatedAt(), TWITTER_FORMAT);
    }

    public static long getMinId(List<Message> messages) {
        long min = 0;
        for (Message message : messages) {
            if (message.getUid() < min || min == 0) {
                min = message.getUid();
            }
        }
        if (messages.size() > 0 && min != messages.get(messages.size() - 1).getUid()) {
            Log.d("DEBUG", "The identified min id is not at the bottom of list!");
        }
        return min;
    }
}