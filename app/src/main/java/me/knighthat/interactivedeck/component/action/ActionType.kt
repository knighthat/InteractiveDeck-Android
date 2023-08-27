package me.knighthat.interactivedeck.component.action

import com.google.gson.JsonPrimitive
import me.knighthat.interactivedeck.json.JsonSerializable

enum class ActionType : JsonSerializable {

    PRESS;

    override fun serialize(): JsonPrimitive {
        return JsonPrimitive(name)
    }
}