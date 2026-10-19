package com.fongmi.android.tv.api.rex;

import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Rex 数据源共用的容错 JSON 助手（不依赖 Android 类，可直接单测）。 */
public final class RexJson {

    private RexJson() {
    }

    @Nullable
    public static JsonElement parse(String body) {
        if (isEmpty(body)) return null;
        try {
            return JsonParser.parseString(body.trim());
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** 解析为对象；失败或非对象返回空对象（调用方无需判空）。 */
    public static JsonObject object(String body) {
        JsonObject object = objectOf(parse(body));
        return object == null ? new JsonObject() : object;
    }

    @Nullable
    public static JsonObject objectOf(@Nullable JsonElement element) {
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    @Nullable
    public static JsonArray arrayOf(@Nullable JsonElement element) {
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : null;
    }

    @Nullable
    public static JsonArray array(@Nullable JsonObject object, String name) {
        if (object == null) return null;
        try {
            return arrayOf(object.get(name));
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Nullable
    public static JsonObject child(@Nullable JsonObject object, String name) {
        if (object == null) return null;
        try {
            return objectOf(object.get(name));
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static String string(@Nullable JsonObject object, String name) {
        if (object == null) return "";
        try {
            JsonElement element = object.get(name);
            return element == null || element.isJsonNull() ? "" : element.getAsString();
        } catch (RuntimeException e) {
            return "";
        }
    }

    public static long longValue(@Nullable JsonObject object, String name) {
        if (object == null) return 0;
        try {
            JsonElement element = object.get(name);
            return element == null || element.isJsonNull() ? 0 : element.getAsLong();
        } catch (RuntimeException e) {
            return 0;
        }
    }

    public static double doubleValue(@Nullable JsonObject object, String name) {
        if (object == null) return 0;
        try {
            JsonElement element = object.get(name);
            return element == null || element.isJsonNull() ? 0 : element.getAsDouble();
        } catch (RuntimeException e) {
            return 0;
        }
    }

    public static boolean isEmpty(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }
}
