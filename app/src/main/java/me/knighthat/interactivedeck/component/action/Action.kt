package me.knighthat.interactivedeck.component.action

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.knighthat.interactivedeck.component.ibutton.IButton

open class Action(type: ActionType, button: IButton) {

    private val button: IButton
    private val type: ActionType

    init {
        this.type = type
        this.button = button
    }

    fun json(): JsonObject {
        val uuid = this.button.uuid().toString()

        val json = JsonObject()
        json.add("action", this.type.json())
        json.add("uuid", JsonPrimitive(uuid))

        return json
    }
}