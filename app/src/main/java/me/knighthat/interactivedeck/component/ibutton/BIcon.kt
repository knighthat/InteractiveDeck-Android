package me.knighthat.interactivedeck.component.ibutton

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.utils.ColorUtils


internal class BIcon {

    var outer = 0
    var inner = 0

    fun update(json: JsonObject) {
        val outerElement = json["outer"]
        if (outerElement != null)
            outer(outerElement)

        val innerElement = json["inner"]
        if (innerElement != null)
            inner(innerElement)
    }

    private fun outer(json: JsonElement) {
        outer = ColorUtils.parseJson(json)
    }

    private fun inner(json: JsonElement) {
        inner = ColorUtils.parseJson(json)
    }

    override fun toString(): String {
        return String.format("BIcon{outer=%s,inner=%s}", outer, inner)
    }
}