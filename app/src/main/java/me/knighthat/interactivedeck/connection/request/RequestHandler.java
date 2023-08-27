package me.knighthat.interactivedeck.connection.request;

import android.content.Intent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.knighthat.interactivedeck.activity.ButtonsLayout;
import me.knighthat.interactivedeck.component.ibutton.IButton;
import me.knighthat.interactivedeck.connection.wireless.WirelessSender;
import me.knighthat.interactivedeck.console.Log;
import me.knighthat.interactivedeck.event.EventHandler;
import me.knighthat.interactivedeck.file.Profile;
import me.knighthat.interactivedeck.vars.Memory;

public class RequestHandler {

    public static void process( @NotNull Request request ) {
        JsonElement content = request.getContent();

        switch ( request.getType() ) {
            case ADD -> handleAdd( content );
            case REMOVE -> handleRemove( content );
            case UPDATE -> handleUpdate( content );
            case PAIR -> handlePairing( content );
        }
    }

    static void handleAdd( @NotNull JsonElement content ) {
        JsonArray array = content.getAsJsonArray();
        array.forEach( pJson -> {
            JsonObject json = pJson.getAsJsonObject();
            Profile profile = Profile.Companion.fromJson( json );
            Memory.Companion.add( profile );
            if ( profile.isDefault() )
                Memory.Companion.setActive( profile );
        } );
    }

    static void handleRemove( @NotNull JsonElement content ) {

    }

    static void handleUpdate( @NotNull JsonElement content ) {
        JsonObject json = content.getAsJsonObject();
        JsonObject payload = json.getAsJsonObject( "payload" );
        String uuidStr = payload.get( "uuid" ).getAsString();
        UUID targetUuid = UUID.fromString( uuidStr );

        switch ( json.get( "target" ).getAsString() ) {
            case "PROFILE" -> {
                Profile profile = Memory.Companion.getProfile( targetUuid );
                if ( profile != null )
                    profile.update( payload );
            }
            case "BUTTON" -> {
                IButton button = Memory.Companion.getButton( targetUuid );
                if ( button != null )
                    EventHandler.post( () -> button.update( payload ) );
            }
            default -> Log.deb( "Unknown target" + json.get( "target" ) );
        }
    }

    static void handlePairing( @NotNull JsonElement content ) {
        JsonArray json = content.getAsJsonArray();
        List<String> ids = new ArrayList<>();

        json.forEach( id -> ids.add( id.getAsString() ) );

        Request request = new AddRequest( ids );
        WirelessSender.send( request );

        EventHandler.post( () -> {
            Intent intent = new Intent( EventHandler.DEF_ACTIVITY, ButtonsLayout.class );
            EventHandler.DEF_ACTIVITY.startActivity( intent );
        } );
    }
}
