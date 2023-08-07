package me.knighthat.interactivedeck.console;

import org.jetbrains.annotations.NotNull;

public class Log {

    public static void info( @NotNull String s ) {
        android.util.Log.i(Thread.currentThread().getName(), s);
    }

    public static void warn( @NotNull String s ) {
        android.util.Log.w(Thread.currentThread().getName(), s);
    }

    public static void err( @NotNull String s ) {
        android.util.Log.e(Thread.currentThread().getName(), s);
    }

    public static void deb( @NotNull String s ) {
        android.util.Log.d(Thread.currentThread().getName(), s);
    }
}
