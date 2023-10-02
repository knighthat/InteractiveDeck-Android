package me.knighthat.interactivedeck.component.ibutton

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.component.LiveProperty
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
) : AppCompatButton(EventHandler.DEF_ACTIVITY), LiveProperty {

    private var backgroundColor = 0
    private var borderColor = 0

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
    
    @MainThread
    override fun update0(json: JsonObject) {
        if (json.has("icon"))
            update0(json["icon"].asJsonObject)

        if (json.has("label"))
            update0(json["label"].asJsonObject)

        if (json.has("background")) {
            this.backgroundColor = fromJson(json, "background")
            repaint()
        }

        if (json.has("border")) {
            this.borderColor = fromJson(json, "border")
            repaint()
        }

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
//        if (json.has("name"))
//            return

        if (json.has("weight")) {
            val weight = when (json["weight"].asString) {
                "bold" -> Typeface.BOLD
                "italic" -> Typeface.ITALIC
                "bold|italic" -> Typeface.BOLD_ITALIC
                else -> Typeface.NORMAL
            }
            setTypeface(null, weight)
        }

        if (json.has("size"))
            textSize = json["size"].asFloat
    }

    private fun repaint() {
        if (borderColor == 0)
            borderColor = backgroundColor

        val background = ShapeDrawable(RectShape())
        background.paint.color = backgroundColor

        val border = GradientDrawable()
        border.shape = GradientDrawable.RECTANGLE
        border.setStroke(3, borderColor)

        val drawable = LayerDrawable(arrayOf(background, border))
        this.background = drawable
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