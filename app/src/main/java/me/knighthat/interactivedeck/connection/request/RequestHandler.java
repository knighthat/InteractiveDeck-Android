package me.knighthat.interactivedeck.connection.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.knighthat.interactivedeck.activity.ButtonsLayout;
import me.knighthat.interactivedeck.activity.DefaultActivity;
import me.knighthat.interactivedeck.component.ibutton.IButton;
import me.knighthat.interactivedeck.connection.wireless.WirelessSender;
import me.knighthat.interactivedeck.console.Log;
import me.knighthat.interactivedeck.file.Profile;
import me.knighthat.interactivedeck.profile.Profiles;

public class RequestHandler {

    public static void process( @NotNull Request request ) {
        JsonElement content = request.getContent();

        switch ( request.getType() ) {
            case ADD:
                handleAdd( content );
                break;
            case REMOVE:
                handleRemove( content );
                break;
            case UPDATE:
                handleUpdate( content );
                break;
            case PAIR:
                handlePairing( content );
                break;
        }
    }

    static void handleAdd( @NotNull JsonElement content ) {
        JsonArray array = content.getAsJsonArray();
        array.forEach( pJson -> {
            JsonObject json = pJson.getAsJsonObject();
            Profile profile = Profile.Companion.fromJson( json );
            Profiles.PROFILES.add( profile );
            if ( profile.isDefault() )
                DefaultActivity.HANDLER.post( () -> ButtonsLayout.Companion.getButtons().set( profile.buttons() ) );
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
            case "PROFILE":
                Profile profile = Profiles.get( targetUuid );
                if ( profile != null )
                    profile.update( payload );
                break;
            case "BUTTON":
                Profiles.PROFILES.forEach( p -> {
                    for ( IButton button : p.buttons() )
                        if ( button.getUuid().equals( targetUuid ) ) {
                            DefaultActivity.HANDLER.post( () -> {
                                button.update( payload );
                            } );
                            break;
                        }
                } );
                break;
            default:
                Log.deb( "Unknown target" + json.get( "target" ) );
        }
    }

    static void handlePairing( @NotNull JsonElement content ) {
        JsonArray json = content.getAsJsonArray();
        List<String> ids = new ArrayList<>();

        json.forEach( id -> ids.add( id.getAsString() ) );

        Request request = new AddRequest( ids );
        WirelessSender.send( request );

        DefaultActivity.INSTANCE.startBtnLayout();
    }
}
