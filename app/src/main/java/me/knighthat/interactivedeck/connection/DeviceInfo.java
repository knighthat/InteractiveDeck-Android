package me.knighthat.interactivedeck.connection;

import android.os.Build;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import me.knighthat.interactivedeck.json.Json;

public class DeviceInfo {

    public static @NotNull JsonObject json() {
        JsonObject json = new JsonObject();
        json.add("brand", Json.parse(Build.BRAND));
        json.add("device", Json.parse(Build.DEVICE));
        json.add("manufacturer", Json.parse(Build.MANUFACTURER));
        json.add("model", Json.parse(Build.MODEL));
        json.add("androidVersion", Json.parse(Build.VERSION.RELEASE));

        return json;
    }
}
