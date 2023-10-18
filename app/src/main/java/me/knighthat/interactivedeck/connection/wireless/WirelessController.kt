package me.knighthat.interactivedeck.connection.wireless

import android.os.Build
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.connection.RequestHandler
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.vars.Settings
import me.knighthat.interactivedeck.vars.Settings.BUFFER
import me.knighthat.lib.connection.Connection
import me.knighthat.lib.connection.request.PairRequest
import me.knighthat.lib.connection.wireless.WirelessReceiver
import me.knighthat.lib.connection.wireless.WirelessSender
import me.knighthat.lib.logging.Log
import java.io.InputStream
import java.net.Socket

class WirelessController(
    private val ip: String,
    private val port: Int
) : Thread() {

    init {
        name = "NET"

        Connection.whenConnectionStatusChanged {
            if (Connection.isConnected())
                return@whenConnectionStatusChanged

            val currentActivity = EventHandler.getCurrentActivity() ?: return@whenConnectionStatusChanged
            currentActivity.finish()
            EventHandler.setCurrentActivity(null)

            if (Connection.getStatus() != Connection.Status.ERROR)
                Settings.saveLastHost(ip, port)

            this.interrupt()
        }
    }

    private fun handleDisconnection(sender: WirelessSender, client: Socket) {
        Connection.setStatus(Connection.Status.DISCONNECTED)

        sender.interrupt()
        client.close()
        name = "NET"
    }

    private fun startSender(client: Socket): WirelessSender {
        val sender = WirelessSender(client.getOutputStream())
        sender.start()

        val json = JsonObject()
        json.addProperty("brand", Build.BRAND)
        json.addProperty("device", Build.DEVICE)
        json.addProperty("manufacturer", Build.MANUFACTURER)
        json.addProperty("model", Build.MODEL)
        json.addProperty("androidVersion", Build.VERSION.RELEASE)
        PairRequest(json).send()

        return sender
    }

    private fun initReceiver(inStream: InputStream) {
        runCatching {
            WirelessReceiver(
                inStream,
                BUFFER,
                RequestHandler()
            ).run()
        }.onFailure {
            Log.exc("Parsing request failed!", it, false)
            Log.reportBug()
        }
    }

    override fun run() {
        Socket(ip, port)
            .runCatching {

                Connection.setStatus(Connection.Status.CONNECTED)

                val sender = startSender(this)
                initReceiver(getInputStream())
                handleDisconnection(sender, this)

                close()

            }.onFailure {

                Connection.setStatus(Connection.Status.ERROR)
                Log.exc("Connection error!", it, false)

            }
    }
}