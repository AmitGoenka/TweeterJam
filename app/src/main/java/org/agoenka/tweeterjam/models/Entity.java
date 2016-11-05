package org.agoenka.tweeterjam.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.agoenka.tweeterjam.TweeterJamDatabase;
import org.parceler.Parcel;

/**
 * Author: agoenka
 * Created At: 11/4/2016
 * Version: ${VERSION}
 */
@Parcel(analyze = Entity.class)
@Table(database = TweeterJamDatabase.class)
public class Entity extends BaseModel {

    @Column @PrimaryKey String mediaUrl;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getEntity() {
        return mediaUrl;
    }

    public Entity() {}

}