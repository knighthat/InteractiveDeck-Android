package me.knighthat.interactivedeck.component.ibutton

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.persistent.Persistent
import me.knighthat.interactivedeck.task.GotoPage
import me.knighthat.interactivedeck.task.Task
import me.knighthat.interactivedeck.utils.ColorUtils
import me.knighthat.lib.component.ibutton.InteractiveButton
import me.knighthat.lib.connection.request.TargetedRequest
import java.util.UUID


@SuppressLint("ViewConstructor")
class IButton(
    override val uuid: UUID,
    override val profile: UUID,
    override val posX: Int,
    override val posY: Int,
    var task: Task?
) : AppCompatButton(EventHandler.DEF_ACTIVITY), InteractiveButton {

    companion object {
        fun fromJson(profile: UUID, json: JsonObject): IButton {
            val uuidStr = json["uuid"].asString
            val uuid = UUID.fromString(uuidStr)
            val x = json.getAsJsonPrimitive("x").asInt
            val y = json.getAsJsonPrimitive("y").asInt

            val button = IButton(uuid, profile, x, y, null)
            button.update(json)

            return button
        }
    }

    private var fill: Int = 0
        set(value) {
            logAndSendUpdate("background", fill, value)

            field = value
            repaint()
        }
    private var border = 0
        set(value) {
            logAndSendUpdate("border", border, value)

            field = value
            repaint()
        }

    private fun font(json: JsonObject) {
        //        if (json.has("name"))
        //            return

        if (json.has("weight")) {
            val weight =
                    when (json["weight"].asString) {
                        "bold"        -> Typeface.BOLD
                        "italic"      -> Typeface.ITALIC
                        "bold|italic" -> Typeface.BOLD_ITALIC
                        else          -> Typeface.NORMAL
                    }
            setTypeface(null, weight)
        }

        if (json.has("size"))
            textSize = json["size"].asFloat
    }

    private fun repaint() {
        if (border == 0)
            border = fill

        val background = ShapeDrawable(RectShape())
        background.paint.color = fill

        val border = GradientDrawable()
        border.shape = GradientDrawable.RECTANGLE
        border.setStroke(3, this.border)

        val drawable = LayerDrawable(arrayOf(background, border))
        this.background = drawable
    }

    private fun task(task: JsonObject?) {
        this.task = null
        if (task == null) return

        val type = task["action_type"].asString
        if (!type.equals("SWITCH_PROFILE"))
            return
        val goto = task["profile"].asString
        val uuid = UUID.fromString(goto)
        this.task = GotoPage(uuid)
    }

    override val target: TargetedRequest.Target = TargetedRequest.Target.BUTTON
    override fun remove() = Persistent.remove(this)

    override fun update(json: JsonObject) = EventHandler.post { update0(json) }
}