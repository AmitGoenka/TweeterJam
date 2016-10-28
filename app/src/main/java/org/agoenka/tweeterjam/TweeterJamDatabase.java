package org.agoenka.tweeterjam;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Author: agoenka
 * Created At: 10/27/2016
 * Version: ${VERSION}
 */
@Database(name = TweeterJamDatabase.NAME, version = TweeterJamDatabase.VERSION)
public class TweeterJamDatabase {
    static final String NAME = "TweeterJamDatabase";
    static final int VERSION = 1;
}