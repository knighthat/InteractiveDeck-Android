package me.knighthat.interactivedeck.component.ibutton

import android.widget.GridLayout
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import me.knighthat.interactivedeck.activity.DefaultActivity
import me.knighthat.interactivedeck.task.GotoPage
import me.knighthat.interactivedeck.task.Task
import me.knighthat.interactivedeck.utils.ColorUtils
import java.util.UUID


class IButton(json: JsonObject) : AppCompatButton(DefaultActivity.INSTANCE) {

    val uuid: UUID
    val x: Int
    val y: Int
    var task: Task? = null

    init {
        if (!json.has("uuid") ||
            !json.has("background") ||
            !json.has("foreground") ||
            !json.has("text") ||
            !json.has("x") ||
            !json.has("y")
        )
            throw JsonSyntaxException("Not enough argument")

        val uuid = json["uuid"].asString
        this.uuid = UUID.fromString(uuid)

        val x = json.getAsJsonPrimitive("x")
        this.x = x.asInt

        val y = json.getAsJsonPrimitive("y")
        this.y = y.asInt

        val params = GridLayout.LayoutParams()
        params.rowSpec = GridLayout.spec(this.y, 1, 1f)
        params.columnSpec = GridLayout.spec(this.x, 1, 1f)
        this.layoutParams = params

        this.update(json)
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