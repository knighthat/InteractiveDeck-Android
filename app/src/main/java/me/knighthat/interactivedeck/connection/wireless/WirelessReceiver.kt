package me.knighthat.interactivedeck.connection.wireless

import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import me.knighthat.interactivedeck.activity.DefaultActivity
import me.knighthat.interactivedeck.json.Json
import java.io.IOException
import java.io.InputStream

class WirelessReceiver(stream: InputStream) {

    private val stream: InputStream
    private val buffer = ByteArray(1024)

    init {
        this.stream = stream
    }

    fun start() {
        try {
            var bytesRead: Int
            var finalStr = ""
            while (stream.read(buffer).also { bytesRead = it } != -1) {
                val decoded = String(buffer, 0, bytesRead)
                if (!parseJson(decoded))
                    finalStr = finalStr.plus(decoded)
                if (parseJson(finalStr))
                    runBlocking {
                        val json = JsonParser.parseString(finalStr).asJsonObject
                        Json.handle(json)
                        DefaultActivity.INSTANCE.startBtnLayout()
                    }
            }
        } catch (e: IOException) {
            //TODO Handle THIS
            e.printStackTrace()
        }
    }

    private fun parseJson(msg: String): Boolean {
        var isJson: Boolean
        runBlocking {
            isJson = try {
                JsonParser.parseString(msg)
                true
            } catch (e: Exception) {
                false
            }
        }
        return isJson
    }
}