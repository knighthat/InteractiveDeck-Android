package me.knighthat.interactivedeck.connection.request

import com.google.gson.JsonArray

class AddRequest(uuids: Collection<String>) : Request(RequestType.ADD, JsonArray(uuids.size)) {

    init {
        uuids.forEach(content.asJsonArray::add)
    }
}