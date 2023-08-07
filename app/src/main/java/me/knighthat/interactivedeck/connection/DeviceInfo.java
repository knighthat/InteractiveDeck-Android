package me.knighthat.interactivedeck.connection;

import android.os.Build;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.jetbrains.annotations.NotNull;

public class DeviceInfo {

    public static @NotNull JsonObject json() {
        JsonPrimitive brand = new JsonPrimitive(Build.BRAND);
        JsonPrimitive device = new JsonPrimitive(Build.DEVICE);
        JsonPrimitive manufacturer = new JsonPrimitive(Build.MANUFACTURER);
        JsonPrimitive model = new JsonPrimitive(Build.MODEL);
        int androidVer = Integer.parseInt(Build.VERSION.RELEASE);
        JsonPrimitive androidVersion = new JsonPrimitive(androidVer);

        JsonObject json = new JsonObject();
        json.add("brand", brand);
        json.add("device", device);
        json.add("manufacturer", manufacturer);
        json.add("model", model);
        json.add("androidVersion", androidVersion);

        return json;
    }
}
