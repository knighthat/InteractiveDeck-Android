package me.knighthat.interactivedeck.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Json {

    @Contract ( "_ -> null" )
    public static @Nullable JsonObject validate( @NotNull String str ) {
        try {
            return JsonParser.parseString(str).getAsJsonObject();
        } catch (JsonParseException e) {
            return null;
        }
    }
}
