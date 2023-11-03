package me.knighthat.interactivedeck.connection.wireless

import android.os.Build
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.connection.RequestHandler
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.persistent.Persistent
import me.knighthat.interactivedeck.vars.Settings
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

    private fun sendPairRequest() {
        val json = JsonObject()
        json.addProperty("brand", Build.BRAND)
        json.addProperty("device", Build.DEVICE)
        json.addProperty("manufacturer", Build.MANUFACTURER)
        json.addProperty("model", Build.MODEL)
        json.addProperty("androidVersion", Build.VERSION.RELEASE)
        PairRequest(json).send()
    }

    private fun setupReceiver(inStream: InputStream) {
        name = "NET/I"

        runCatching {
            WirelessReceiver(
                inStream,
                Settings.BUFFER,
                RequestHandler()
            ).run()
        }.onFailure {
            if (it is SocketException && Connection.isDisconnected())
                return

            Log.exc("Parsing request failed!", it, true)
            Log.reportBug()
        }
    }

    private fun addConnectionWatcher(client: Socket) {
        Connection.whenConnectionStatusChanged(object : Observer<Connection.Status> {
            override fun update(oldValue: Connection.Status?, newValue: Connection.Status?) {
                // Skip this if connected is established
                if (Connection.isConnected())
                    return
                
                Persistent.free()

                val currentActivity = EventHandler.getCurrentActivity() ?: return
                currentActivity.finish()
                EventHandler.setCurrentActivity(null)

                if (Connection.isDisconnected()) {
                    Settings.saveLastHost(ip, port)
                    client.close()
                }

                this@WirelessController.interrupt()
            }
        })
    }

    override fun run() {
        Socket(ip, port)
            .runCatching {

                Connection.status = Connection.Status.CONNECTED

                val sender = WirelessSender(getOutputStream())
                sender.start()
                addConnectionWatcher(this)

                sendPairRequest()
                setupReceiver(getInputStream())

                name = "NET"
                Connection.status = Connection.Status.DISCONNECTED
                sender.interrupt()

                close()

            }.onFailure {

                Connection.status = Connection.Status.ERROR
                Log.exc("Connection error!", it, false)

            }
    }
}