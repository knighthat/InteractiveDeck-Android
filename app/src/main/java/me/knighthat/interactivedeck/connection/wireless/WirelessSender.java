package me.knighthat.interactivedeck.connection.wireless;

import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;

public class WirelessSender {

    private static @Nullable OutputStream stream;

    public static void init( @NotNull OutputStream stream ) {
        WirelessSender.stream = stream;
        send(getModel());
        send(getAndroidVer());
    }

    public static void send( @NotNull String msg ) {
        if (stream == null) {
            Log.d("[NET]", "Socket has closed!");
            return;
        }
        try {
            stream.write(msg.getBytes());
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static @NotNull String getModel() {
        return "device_model: " + Build.MODEL;
    }

    private static @NotNull String getAndroidVer() {
        return "android_version: " + Build.VERSION.RELEASE;
    }
}
