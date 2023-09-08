package me.knighthat.interactivedeck.component.ibutton

import android.widget.GridLayout
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.task.GotoPage
import me.knighthat.interactivedeck.task.Task
import me.knighthat.interactivedeck.utils.ColorUtils
import java.util.UUID


data class IButton(
    val uuid: UUID,
    val x: Int,
    val y: Int,
    var task: Task?
) : AppCompatButton(EventHandler.DEF_ACTIVITY) {

    companion object {
        fun fromJson(json: JsonObject): IButton {
            val uuidStr = json["uuid"].asString
            val uuid = UUID.fromString(uuidStr)
            val x = json.getAsJsonPrimitive("x").asInt
            val y = json.getAsJsonPrimitive("y").asInt

            val button = IButton(uuid, x, y, null)
            button.update(json)

            return button
        }
    }

    init {
        val params = GridLayout.LayoutParams()
        params.rowSpec = GridLayout.spec(y, 1, 1f)
        params.columnSpec = GridLayout.spec(x, 1, 1f)

        layoutParams = params
    }

    fun update(json: JsonObject) = EventHandler.post { update0(json) }

    @MainThread
    private fun update0(json: JsonObject) {
        if (json.has("icon"))
            update0(json["icon"].asJsonObject)

        if (json.has("label"))
            update0(json["label"].asJsonObject)

        if (json.has("background")) {
            val color = fromJson(json, "background")
            setBackgroundColor(color)
        }

        if (json.has("border"))
            border(json["border"].asJsonArray)

        if (json.has("foreground")) {
            val color = fromJson(json, "foreground")
            setTextColor(color)
        }

        if (json.has("font"))
            font(json["font"].asJsonObject)

        if (json.has("text"))
            text = json["text"].asString

        if (json.has("task")) {
            this.task = null
            if (!json["task"].isJsonNull)
                task(json["task"].asJsonObject)
        }
    }

    private fun font(json: JsonObject) {
        // TODO Implement this
        return
    }

    private fun border(array: JsonArray) {
        // TODO Implement this
        return
    }

    private fun fromJson(json: JsonObject, property: String): Int {
        val array = json[property].asJsonArray
        return ColorUtils.parseJson(array)
    }

    private fun task(task: JsonObject) {
        val type = task["action_type"].asString
        if (!type.equals("SWITCH_PROFILE"))
            return
        val goto = task["profile"].asString
        val uuid = UUID.fromString(goto)
        this.task = GotoPage(uuid)
    }
}