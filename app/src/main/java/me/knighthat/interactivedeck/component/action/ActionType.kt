package me.knighthat.interactivedeck.component.action

import com.google.gson.JsonPrimitive

enum class ActionType {

    PRESS;

    fun json(): JsonPrimitive {
        return JsonPrimitive(name)
    }
}