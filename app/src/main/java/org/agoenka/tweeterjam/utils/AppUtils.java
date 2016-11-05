package org.agoenka.tweeterjam.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Author: agoenka
 * Created At: 11/5/2016
 * Version: ${VERSION}
 */

public class AppUtils {

    private AppUtils() {
        //no instance
    }

    public static boolean missingWritePermission(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context);
    }

    public static boolean hasWritePermission(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static Intent getPermissionIntent(Context context) {
        return new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                .setData(Uri.parse("package:" + context.getPackageName()));
    }
}