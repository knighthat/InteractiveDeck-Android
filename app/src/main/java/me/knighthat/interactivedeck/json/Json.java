package me.knighthat.interactivedeck.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Json {

    private static final @NotNull Gson GSON;

    static {
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }

    @Contract ( "_ -> null" )
    public static @Nullable JsonObject validate( @NotNull String str ) {
        try {
            return JsonParser.parseString( str ).getAsJsonObject();
        } catch ( JsonParseException e ) {
            return null;
        }
    }

    public static @NotNull JsonElement parse( @Nullable Object obj ) {
        JsonElement element;
        if ( obj == null ) {
            element = JsonNull.INSTANCE;
        } else if ( obj instanceof Number ) {
            element = new JsonPrimitive( (Number) obj );
        } else if ( obj instanceof Boolean ) {
            element = new JsonPrimitive( (Boolean) obj );
        } else if ( obj instanceof Iterable<?> ) {
            element = new JsonArray();
            ( (Iterable<?>) obj ).forEach( e -> ( (JsonArray) element ).add( parse( e ) ) );
        } else {
            element = new JsonPrimitive( String.valueOf( obj ) );
        }
        return element;
    }

    public static void save( @NotNull JsonObject json, @NotNull File file ) throws IOException {
        FileWriter writer = new FileWriter( file );
        GSON.toJson( json, writer );
        writer.close();
    }
}
