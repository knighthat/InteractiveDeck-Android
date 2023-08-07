package me.knighthat.interactivedeck.connection.request

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.json.JsonSerializable

open class Request(val type: RequestType, val content: JsonElement) : JsonSerializable {

    companion object {
        fun parse(json: JsonObject): Request {
            val typeStr = json.get("type").asString
            val type = RequestType.valueOf(typeStr)

            val content = json.get("content")

            return Request(type, content)
        }
    }

    override fun serialize(): String {
        val json = JsonObject()
        json.add("type", this.type.json())
        json.add("content", this.content)

        return json.toString()
    }
}