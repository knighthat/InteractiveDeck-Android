package me.knighthat.interactivedeck.component.ibutton

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.utils.ColorUtils


internal class BLabel {

    var color = 0
    var text = ""
    var fontName = "Arial"
    var fontWeight = 0
    var fontSize = 14

    fun update(json: JsonObject) {
        val colorElement = json["color"]
        if (colorElement != null)
            color(colorElement)

        val textElement = json["text"]
        if (colorElement != null)
            text(textElement)

        val fontElement = json["font"]
        if (fontElement != null)
            font(fontElement)
    }

    private fun color(json: JsonElement) {
        this.color = ColorUtils.parseJson(json)
    }

    private fun text(json: JsonElement) {
        this.text = json.asString
    }

    private fun font(json: JsonElement) {
        val font = json.asJsonObject
        fontName = font["name"].asString
        fontSize = font["size"].asInt
        fontWeight = when (font["weight"].asString) {
            "bold" -> 1
            "italic" -> 2
            else -> 0
        }
    }

    override fun toString(): String {
        val template = "BLabel{text:%s,color=%s,font=[%s,%s,%s]}"
        return String.format(template, text, color, fontName, fontWeight, fontSize)
    }
}