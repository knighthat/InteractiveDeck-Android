package me.knighthat.interactivedeck.connection.request

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.connection.wireless.WirelessController
import me.knighthat.interactivedeck.connection.wireless.WirelessSender
import java.util.UUID

open class Request(
    val type: RequestType,
    val content: JsonElement
) {

    companion object {
        fun fromJson(json: JsonObject): Request {
            val typeStr = json.get("type").asString
            val type = RequestType.valueOf(typeStr)
            val content = json.get("content")

            val isTargetedRequest = json.has("target") && json.has("uuid")
            var target: TargetedRequest.Target? = null
            var uuid: UUID? = null

            if (isTargetedRequest) {
                val targetString = json["target"].asString
                target = TargetedRequest.Target.valueOf(targetString)

                if (!json["uuid"].equals(JsonNull.INSTANCE)) {
                    val uuidStr = json["uuid"].asString
                    uuid = UUID.fromString(uuidStr)
                }
            }

            return if (isTargetedRequest) {
                TargetedRequest(type, target!!, uuid, content)
            } else
                Request(type, content)
        }
    }

    fun send() {
        if (WirelessController.SOCKET == null)
            return

        if (WirelessController.SOCKET!!.isConnected)
            WirelessSender.send(this)
    }

    override fun toString(): String {
        val json = JsonObject()
        json.addProperty("type", type.name)
        json.add("content", this.content)

        return json.toString()
    }

    enum class RequestType {
        ADD, REMOVE, UPDATE, PAIR, ACTION;
    }
}