package org.agoenka.tweeterjam.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.SECOND_IN_MILLIS;

/**
 * Full credit goes to : https://github.com/ewilden/TimeFormatter
 */

public class DateUtils {

    public static final String TWITTER_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"; //"Mon Apr 01 21:16:23 +0000 2014"

    public static String getDuration(String date, String format) {
        long time = getTime(date, format);
        long diff = (System.currentTimeMillis() - time) / 1000;
        if (diff < 5)
            return "Just now";
        else if (diff < 60)
            return String.format(Locale.ENGLISH, "%ds", diff);
        else if (diff < 60 * 60)
            return String.format(Locale.ENGLISH, "%dm", diff / 60);
        else if (diff < 60 * 60 * 24)
            return String.format(Locale.ENGLISH, "%dh", diff / (60 * 60));
        else if (diff < 60 * 60 * 24 * 30)
            return String.format(Locale.ENGLISH, "%dd", diff / (60 * 60 * 24));
        else {
            Calendar now = Calendar.getInstance();
            Calendar then = getCalendar(time);
            if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
                return String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                        + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            } else {
                return String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                        + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " "
                        + String.valueOf(then.get(Calendar.YEAR) - 2000);
            }
        }
    }

    private static long getTime(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            sdf.setLenient(true);
            return sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static Calendar getCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    @SuppressWarnings("unused")
    public static String getRelativeDuration(String date) {
        long millis = getTime(date, TWITTER_FORMAT);
        if (millis > 0) {
            return getRelativeDuration(millis, SECOND_IN_MILLIS, FORMAT_ABBREV_ALL);
        } else {
            return "";
        }
    }

    private static String getRelativeDuration(long time, long resolution, int flags) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), resolution, flags).toString();
    }

}