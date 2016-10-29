package org.agoenka.tweeterjam.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Author: agoenka
 * Created At: 10/22/2016
 * Version: ${VERSION}
 */

public class ConnectivityUtils {

    private ConnectivityUtils() {
        //no instance
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * This function pings the Google DNS servers to check for the expected exit value
     * Note that this does not need to be run in background and does not require special privileges
     * @see [http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-the-internet-is-connected]
     * @return whether the device is online or not
     */
    private static boolean isOnline() {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "Network not available.", Toast.LENGTH_SHORT).show();
        } else if (!isOnline()) {
            Toast.makeText(context, "Unable to connect to internet.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("DEBUG", "Connected to network and internet is available");
            return true;
        }
        return false;
    }
}
