package me.knighthat.interactivedeck.connection.wireless

import android.os.Build
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.connection.RequestHandler
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.persistent.Persistent
import me.knighthat.interactivedeck.vars.Settings
import me.knighthat.interactivedeck.vars.Settings.BUFFER
import me.knighthat.lib.connection.Connection
import me.knighthat.lib.connection.request.PairRequest
import me.knighthat.lib.connection.wireless.WirelessReceiver
import me.knighthat.lib.connection.wireless.WirelessSender
import me.knighthat.lib.logging.Log
import me.knighthat.lib.observable.Observer
import java.io.InputStream
import java.net.Socket
import java.net.SocketException

class WirelessController(
    private val ip: String,
    private val port: Int
) : Thread() {

    init {
        name = "NET"

        Connection.whenConnectionStatusChanged(object : Observer<Connection.Status> {
            override fun update(oldValue: Connection.Status?, newValue: Connection.Status?) {
                if (Connection.isConnected())
                    return

                val currentActivity = EventHandler.getCurrentActivity() ?: return
                currentActivity.finish()
                EventHandler.setCurrentActivity(null)

                if (!Connection.isError())
                    Settings.saveLastHost(ip, port)

                Persistent.free()
                this@WirelessController.interrupt()
            }
        })
    }

    private fun handleDisconnection(sender: WirelessSender, client: Socket) {
        Connection.status = Connection.Status.DISCONNECTED

        sender.interrupt()
        client.close()
        name = "NET"
    }

    private fun startSender(client: Socket): WirelessSender {
        val sender = WirelessSender(client.getOutputStream())
        Connection.whenConnectionStatusChanged(object : Observer<Connection.Status> {
            override fun update(oldValue: Connection.Status?, newValue: Connection.Status?) {
                if (Connection.isDisconnected())
                    client.close()
            }
        })
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
            if (it is SocketException && !Connection.isConnected())
                return

            Log.exc("Parsing request failed!", it, true)
            Log.reportBug()
        }
    }

    override fun run() {
        Socket(ip, port)
            .runCatching {

                Connection.status = Connection.Status.CONNECTED

                val sender = startSender(this)
                initReceiver(getInputStream())
                handleDisconnection(sender, this)

                close()

            }.onFailure {

                Connection.status = Connection.Status.ERROR
                Log.exc("Connection error!", it, false)

            }
    }
}