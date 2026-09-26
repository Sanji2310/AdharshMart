package com.adharsh.adharshmart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Centralized Gson instance (rule: Gson for all JSON serialization) with java.time support. */
public final class JsonUtil {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final JsonSerializer<LocalDateTime> DATE_TIME_SERIALIZER =
            (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.format(ISO));

    private static final JsonDeserializer<LocalDateTime> DATE_TIME_DESERIALIZER =
            (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), ISO);

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, DATE_TIME_SERIALIZER)
            .registerTypeAdapter(LocalDateTime.class, DATE_TIME_DESERIALIZER)
            .create();

    private JsonUtil() {
    }

    public static Gson gson() {
        return GSON;
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(java.io.Reader reader, Type type) {
        return GSON.fromJson(reader, type);
    }
}
