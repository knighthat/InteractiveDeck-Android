package me.knighthat.interactivedeck.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import me.knighthat.interactivedeck.component.Buttons;

public class Json {

    public static void handle( @NotNull JsonObject json ) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet())
            Buttons.add(entry.getKey(), entry.getValue());
    }
}
