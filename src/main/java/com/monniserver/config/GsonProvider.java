package com.monniserver.config;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;

public class GsonProvider {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                    return LocalDate.parse(json.getAsString());
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.toString()); // yyyy-MM-dd
                }
            })
            .create();

    public static Gson getGson() {
        return gson;
    }
}
