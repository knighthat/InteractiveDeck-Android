package me.knighthat.interactivedeck.connection.wireless

import kotlinx.coroutines.runBlocking
import me.knighthat.interactivedeck.activity.DefaultActivity
import me.knighthat.interactivedeck.connection.request.PairRequest
import me.knighthat.interactivedeck.connection.request.Request
import me.knighthat.interactivedeck.connection.request.RequestHandler
import me.knighthat.interactivedeck.console.Log
import me.knighthat.interactivedeck.json.Json
import me.knighthat.interactivedeck.vars.Settings
import me.knighthat.interactivedeck.vars.Settings.BUFFER
import java.io.IOException
import java.io.InputStream
import java.net.ConnectException
import java.net.Socket


class WirelessController(ip: String, port: Int) : Thread() {

    companion object {
        var SOCKET: Socket? = null
    }

    private val ip: String
    private val port: Int

    init {
        this.ip = ip
        this.port = port
        name = "NET"
    }

    override fun run() {
        try {
            Socket(ip, port)
                .also { SOCKET = it }
                .use {
                    WirelessSender.start(it.getOutputStream())
                    WirelessSender.send(PairRequest())

                    name = "NET/I"
                    this.handIncomingTraffic(it.getInputStream())
                    name = "NET"

                }
            SOCKET = null
        } catch (e: IOException) {
            //TODO Handle exception
            e.printStackTrace()
        } catch (e: ConnectException) {
            //TODO Handle timeout
            Log.deb("Connection timeout! Resetting...")
            DefaultActivity.toast("Connection timeout!")
            interrupt()
        } finally {
            Settings.saveLastHost(ip, port)
        }
    }

    private fun handIncomingTraffic(stream: InputStream) {
        var bytesRead: Int
        var finalStr = ""
        while (stream.read(BUFFER).also { bytesRead = it } != -1) {
            val decoded = try {
                String(BUFFER, 0, bytesRead)
            } catch (e: StringIndexOutOfBoundsException) {
                break
            }

            Log.deb("Received from host:")
            Log.deb(decoded)

            runBlocking {
                finalStr = finalStr.plus(decoded)

                val json = Json.validate(finalStr)
                if (json != null) {
                    val request = Request.parse(json)
                    RequestHandler.process(request)
                    finalStr = ""
                }
            }
        }
    }
}