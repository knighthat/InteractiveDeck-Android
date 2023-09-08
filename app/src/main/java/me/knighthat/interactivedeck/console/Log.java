package me.knighthat.interactivedeck.console;

import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import me.knighthat.interactivedeck.event.EventHandler;

public class Log {

    public static void log( @NotNull LogLevel level, @NotNull String s, boolean toast ) {
        String thread = Thread.currentThread().getName();
        switch ( level ) {
            case DEBUG -> android.util.Log.d( thread, s );
            case INFO -> android.util.Log.i( thread, s );
            case WARNING -> android.util.Log.w( thread, s );
            case ERROR -> android.util.Log.e( thread, s );
        }
        if ( !toast )
            return;

        int duration = level.rank > 1 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        toast( s, duration );
    }

    public static void deb( @NotNull String s ) {
        log( LogLevel.DEBUG, s, false );
    }

    public static void info( @NotNull String s, boolean toast ) {
        log( LogLevel.INFO, s, toast );
    }

    public static void warn( @NotNull String s ) {
        log( LogLevel.WARNING, s, true );
    }

    public static void err( @NotNull String s, boolean toast ) {
        log( LogLevel.ERROR, s, toast );
    }

    public static void toast( @NotNull String s, int duration ) {
        EventHandler.post( () -> Toast.makeText( EventHandler.DEF_ACTIVITY, s, duration ).show() );
        log( LogLevel.INFO, "TOAST: " + s, false );
    }

    public enum LogLevel {
        DEBUG( 0 ), INFO( 1 ), WARNING( 2 ), ERROR( 3 );

        final int rank;

        LogLevel( int rank ) {
            this.rank = rank;
        }
    }
}
