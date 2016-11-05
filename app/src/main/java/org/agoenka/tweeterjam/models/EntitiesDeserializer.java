package org.agoenka.tweeterjam.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Author: agoenka
 * Created At: 11/4/2016
 * Version: ${VERSION}
 */
public class EntitiesDeserializer implements JsonDeserializer<Entity> {

    @Override
    public Entity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Entity entity = new Entity();
        if (json != null) {
            JsonElement mediaElement = json.getAsJsonObject().get("media");
            if (mediaElement != null) {
                JsonArray mediaArray = mediaElement.getAsJsonArray();
                if (mediaArray != null) {
                    for(int i = 0; i < mediaArray.size(); i++) {
                        JsonObject mediaObject = mediaArray.get(i).getAsJsonObject();
                        String type = mediaObject.get("type").getAsString();
                        if ("photo".equalsIgnoreCase(type)) {
                            entity.mediaUrl = mediaObject.get("media_url").getAsString();
                            break;
                        }
                    }
                }
            }
        }
        return entity;
    }
}