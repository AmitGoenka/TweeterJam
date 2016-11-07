package org.agoenka.tweeterjam.models;

import org.parceler.Parcel;

import java.util.List;

/**
 * Author: agoenka
 * Created At: 11/6/2016
 * Version: ${VERSION}
 */
@Parcel(analyze = Users.class)
public class Users {

    List<User> users;
    long nextCursor;

    public List<User> getUsers() {
        return users;
    }

    public long getNextCursor() {
        return nextCursor;
    }

    public Users() {}
}