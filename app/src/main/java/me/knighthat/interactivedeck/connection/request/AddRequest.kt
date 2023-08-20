package me.knighthat.interactivedeck.connection.request

import com.google.gson.JsonArray
import me.knighthat.interactivedeck.json.Json

class AddRequest(uuids: Iterable<String>) : Request(RequestType.ADD, JsonArray()) {

    init {
        uuids.forEach { content.asJsonArray.add(Json.parse(it)) }
    }
}