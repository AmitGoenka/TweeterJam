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
@Parcel(analyze = ExtendedEntity.class)
@Table(database = TweeterJamDatabase.class)
public class ExtendedEntity extends BaseModel {

    @Column @PrimaryKey String videoUrl;
    @Column String videoContentType;

    @SuppressWarnings("unused")
    public String getVideoUrl() {
        return videoUrl;
    }

    @SuppressWarnings("unused")
    public String getVideoContentType() {
        return videoContentType;
    }

    public String getExtendedEntity() {
        return videoUrl;
    }

    public ExtendedEntity() {}

}