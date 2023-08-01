package me.knighthat.interactivedeck.utils;

import android.graphics.Color;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ColorUtils {

    public static int parseJson( @NotNull JsonElement json ) {
        if (!json.isJsonArray()) return 0;
        JsonArray array = json.getAsJsonArray();
        if (array.size() < 3)
            array.add(0);
        int r = array.get(0).getAsInt();
        int g = array.get(1).getAsInt();
        int b = array.get(2).getAsInt();

        return Color.rgb(r, g, b);
    }

    public static int randomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        return Color.rgb(r, g, b);
    }
}
