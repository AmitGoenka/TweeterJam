package org.agoenka.tweeterjam.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.agoenka.tweeterjam.models.EntitiesDeserializer;
import org.agoenka.tweeterjam.models.Entity;
import org.agoenka.tweeterjam.models.ExtendedEntitiesDeserializer;
import org.agoenka.tweeterjam.models.ExtendedEntity;

/**
 * Author: agoenka
 * Created At: 11/4/2016
 * Version: ${VERSION}
 */

public class GsonUtils {

    private GsonUtils() {
        //no instance
    }

    public static Gson getGson() {
       return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Entity.class, new EntitiesDeserializer())
                .registerTypeAdapter(ExtendedEntity.class, new ExtendedEntitiesDeserializer())
                .create();
    }

}