package me.knighthat.interactivedeck.connection.request

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

enum class RequestType {

    ADD, REMOVE, UPDATE, PAIR, ACTION;

    fun json(): JsonElement {
        return JsonPrimitive(name)
    }
}