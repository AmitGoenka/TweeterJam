package org.agoenka.tweeterjam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Author: agoenka
 * Created At: 10/31/2016
 * Version: ${VERSION}
 */

public class SharedPreferencesUtils {

    public static final String KEY_DRAFT_TWEET = "draftTweet";
    public static final String KEY_IN_REPLY_TO_TWEET = "inReplyToTweet";
    public static final String KEY_IN_REPLY_TO_USER = "inReplyToUser";

    private SharedPreferencesUtils() {
        //no instance
    }

    public static void clear(Context context, String... keys) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, null);
    }

    public static long getLong(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(key, 0);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putLong(key, value);
        edit.apply();
    }
}