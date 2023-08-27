package me.knighthat.interactivedeck.component.ibutton

import android.widget.GridLayout
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
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
            if (!json.has("uuid") ||
                !json.has("background") ||
                !json.has("foreground") ||
                !json.has("text") ||
                !json.has("x") ||
                !json.has("y")
            )
                throw JsonSyntaxException("Not enough argument")

            val uuidStr = json["uuid"].asString
            val uuid = UUID.fromString(uuidStr)
            val x = json.getAsJsonPrimitive("x").asInt
            val y = json.getAsJsonPrimitive("y").asInt

            val button = IButton(uuid, x, y, null)

            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(y, 1, 1f)
            params.columnSpec = GridLayout.spec(x, 1, 1f)
            button.layoutParams = params

            button.update(json)

            return button
        }
    }

    fun update(json: JsonObject) {
        val background = json["background"]
        setBackgroundColor(ColorUtils.parseJson(background))

        val foreground = json["foreground"]
        setTextColor(ColorUtils.parseJson(foreground))

        text = json["text"].asString

        if (json.has("goto")) {
            val uuidStr = json["goto"].asString
            val gotoUuid = UUID.fromString(uuidStr)
            this.task = GotoPage(gotoUuid)
        }
    }
}