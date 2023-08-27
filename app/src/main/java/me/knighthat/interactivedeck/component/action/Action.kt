package me.knighthat.interactivedeck.component.action

import com.google.gson.JsonObject
import me.knighthat.interactivedeck.component.ibutton.IButton
import me.knighthat.interactivedeck.json.JsonSerializable

open class Action(
    private val type: ActionType,
    private val button: IButton
) : JsonSerializable {

    override fun serialize(): JsonObject {
        val json = JsonObject()
        json.add("action", type.serialize())
        json.addProperty("uuid", button.uuid.toString())

        return json
    }
}