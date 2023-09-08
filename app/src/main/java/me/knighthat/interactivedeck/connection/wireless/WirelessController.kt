package me.knighthat.interactivedeck.connection.wireless

import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import me.knighthat.interactivedeck.connection.request.PairRequest
import me.knighthat.interactivedeck.connection.request.Request
import me.knighthat.interactivedeck.connection.request.RequestHandler
import me.knighthat.interactivedeck.console.Log
import me.knighthat.interactivedeck.vars.Settings
import me.knighthat.interactivedeck.vars.Settings.BUFFER
import java.io.IOException
import java.io.InputStream
import java.net.ConnectException
import java.net.Socket


class WirelessController(ip: String, port: Int) : Thread() {

    companion object {
        val handler = RequestHandler()
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
                    this.handleIncomingTraffic(it.getInputStream())
                    name = "NET"
                }
            SOCKET = null
        } catch (e: IOException) {
            //TODO Handle exception
            e.printStackTrace()
        } catch (e: ConnectException) {
            //TODO Handle timeout
            Log.warn("Connection timeout!")
            interrupt()
        } finally {
            Settings.saveLastHost(ip, port)
        }
    }

    private fun handleIncomingTraffic(stream: InputStream) {
        var bytesRead: Int
        var finalStr = ""
        while (stream.read(BUFFER).also { bytesRead = it } != -1) {
            val decoded = String(BUFFER, 0, bytesRead)
            val sliced = decoded.split("\u0000")

            if (sliced.size > 1)
                for ((i, v) in sliced.withIndex())
                    when (i) {
                        0 -> {
                            finalStr = finalStr.plus(v)
                            process(finalStr)
                        }

                        sliced.size - 1 -> finalStr = v

                        else -> process(v)
                    }
            else
                finalStr = finalStr.plus(decoded)
        }
    }

    private fun process(payload: String) {
        Log.deb("Processing: $payload")
        runBlocking {
            try {
                val json = JsonParser.parseString(payload).asJsonObject
                val request = Request.fromJson(json)
                handler.process(request)
            } catch (e: JsonParseException) {
                Log.err("Error occurs while parsing request", false)
                Log.err("Caused by: ${e.message}", false)
            }
        }
    }
}