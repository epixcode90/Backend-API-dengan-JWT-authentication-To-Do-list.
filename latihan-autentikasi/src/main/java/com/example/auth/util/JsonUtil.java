package com.example.auth.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.json.JavalinGson;

public class JsonUtil {



    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

}
