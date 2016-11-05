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
public class ExtendedEntitiesDeserializer implements JsonDeserializer<ExtendedEntity> {

    @Override
    public ExtendedEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ExtendedEntity extendedEntity = new ExtendedEntity();
        if (json != null) {
            JsonElement mediaElement = json.getAsJsonObject().get("media");
            if (mediaElement != null) {
                JsonArray mediaArray = mediaElement.getAsJsonArray();
                if (mediaArray != null) {
                    for (int i = 0; i < mediaArray.size(); i++) {
                        JsonObject mediaObject = mediaArray.get(i).getAsJsonObject();
                        String type = mediaObject.get("type").getAsString();
                        if ("video".equalsIgnoreCase(type)) {
                            JsonElement videoInfoElement  = mediaObject.get("video_info");
                            if (videoInfoElement != null) {
                                JsonElement variantsElement = videoInfoElement.getAsJsonObject().get("variants");
                                if (variantsElement != null) {
                                    JsonObject variantsObject = variantsElement.getAsJsonArray().get(0).getAsJsonObject();
                                    extendedEntity.videoContentType = variantsObject.get("content_type").getAsString();
                                    extendedEntity.videoUrl = variantsObject.get("url").getAsString();
                                }
                            }
                        }
                    }
                }
            }
        }
        return extendedEntity;
    }
}