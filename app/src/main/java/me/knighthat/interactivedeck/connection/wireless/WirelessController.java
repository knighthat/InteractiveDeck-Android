package me.knighthat.interactivedeck.connection.wireless;

import android.util.Log;

import androidx.annotation.IntRange;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.Socket;

import me.knighthat.interactivedeck.activity.DefaultActivity;

public class WirelessController extends Thread {

    public static @Nullable Thread THREAD;
    public static @Nullable Socket SOCKET;

    private final @NotNull String address;
    private final @IntRange ( from = 0x1, to = 0xffff ) int port;

    public WirelessController( @NotNull String address, @IntRange ( from = 0x1, to = 0xffff ) int port ) {
        this.address = address;
        this.port = port;
    }

    public static void connect( @NotNull TextInputEditText ipInput, @NotNull TextInputEditText portInput ) {
        if (ipInput.getText() == null || portInput.getText() == null)
            return;

        String ipAddr = ipInput.getText().toString();
        String portStr = portInput.getText().toString();
        int port = Integer.parseInt(portStr);

        if (port < 0x1 || port > 0xffff) {
            String msg = "Port must be a number between 1 and 65535";
            Log.d("[NET]", msg);
            DefaultActivity.toast(msg);
            return;
        }

        THREAD = new WirelessController(ipAddr, port);
        THREAD.start();
    }

    @Override
    public void run() {
        try {
            SOCKET = new Socket(this.address, this.port);
            WirelessSender.init(SOCKET.getOutputStream());
            new WirelessReceiver(SOCKET.getInputStream()).start();

        } catch (IOException e) {
            //TODO Handle THIS
            e.printStackTrace();
        }
    }
}
