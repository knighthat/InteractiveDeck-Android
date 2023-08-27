package me.knighthat.interactivedeck.vars;

import android.content.SharedPreferences;

import me.knighthat.interactivedeck.console.Log;

public class Settings {

    public static SharedPreferences PREFERENCES;

    public static byte[] BUFFER = new byte[1024];

    public static void saveLastHost( String address, int port ) {
        Log.deb( "Saving " + address + ":" + port );

        SharedPreferences.Editor editor = PREFERENCES.edit();
        editor.putString( "address", address );
        editor.putString( "port", String.valueOf( port ) );
        editor.apply();

        String msg = "Saved %s:%s for next login";
        msg = String.format( msg, address(), port() );
        Log.info( msg, true );
    }

    public static String address() {
        return PREFERENCES.getString( "address", "" );
    }

    public static String port() {
        return PREFERENCES.getString( "port", "9129" );
    }
}
