package me.knighthat.interactivedeck.component.ibutton

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Looper
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
import me.knighthat.lib.logging.Log
import org.intellij.lang.annotations.MagicConstant
import java.util.Objects
import java.util.UUID


@SuppressLint("ViewConstructor")
class IButton(
    override val uuid: UUID,
    override val profile: UUID,
    override val posX: Int,
    override val posY: Int
) : AppCompatButton(EventHandler.DEF_ACTIVITY), InteractiveButton {

    companion object {
        fun fromJson(profile: UUID, json: JsonObject): IButton {
            val uuidStr = json["uuid"].asString
            val uuid = UUID.fromString(uuidStr)
            val x = json.getAsJsonPrimitive("x").asInt
            val y = json.getAsJsonPrimitive("y").asInt

            val button = IButton(uuid, profile, x, y)
            button.update(json)

            return button
        }
    }

    var task: Task? = null
        set(value) {
            // If new task equals current task, then do nothing
            if (Objects.equals(task, value))
                return

            logUpdate("task", task, value)
            if (value is GotoPage)
                field = GotoPage(value.uuid)
        }

    private var fill: Int = 0
        set(value) {
            // If new color is the same as the old one, then do nothing
            if (fill == value) return

            logUpdate("background", fill, value)

            field = value
            repaint()
        }
    private var border = 0
        set(value) {
            // If new color is the same as the old one, then do nothing
            if (border == value) return

            logUpdate("border", border, value)

            field = value
            repaint()
        }

    private var label: CharSequence
        get() = text
        set(value) {
            // If text does not change, then do nothing
            if (label == value) return

            logUpdate("text", label, value)
            text = value
        }

    private var weight: Int
        get() = typeface.style
        @MagicConstant(valuesFromClass = Typeface::class)
        set(value) {
            // If new weight equals current one, then do nothing
            if (weight == value) return

            logUpdate("weight", weight, value)
            setTypeface(null, value)
        }

    private var size: Float
        get() = textSize
        set(value) {
            // If new size is the same as old size, then do nothing
            if (size == value) return

            logUpdate("size", size, value)
            textSize = value
        }

    private var fontColor: Int
        get() = currentTextColor
        set(value) {
            // If new color is indifferent, then do nothing
            if (fontColor == value) return

            logUpdate("foreground", fontColor, value)
            setTextColor(value)
        }

    private fun font(json: JsonObject) {
        //        if (json.has("name"))
        //            return

        if (json.has("weight"))
            weight =
                    when (json["weight"].asString) {
                        "bold"        -> Typeface.BOLD
                        "italic"      -> Typeface.ITALIC
                        "bold|italic" -> Typeface.BOLD_ITALIC
                        else          -> Typeface.NORMAL
                    }

        if (json.has("size"))
            size = json["size"].asFloat
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

    override val target: TargetedRequest.Target = TargetedRequest.Target.BUTTON
    override fun remove() = Persistent.remove(this)

    override fun logUpdate(property: String, oldValue: Any?, newValue: Any?) {
        val old =
                when (oldValue) {
                    is Int -> String.format("#%06X", 0xFFFFFF and oldValue)
                    else   -> oldValue.toString()
                }
        val new =
                when (newValue) {
                    is Int -> String.format("#%06X", 0xFFFFFF and newValue)
                    else   -> newValue.toString()
                }
        Log.deb("Button@[x=$posX,y=$posY] changed $property from \"$old\" to \"$new\"")
    }

    @MainThread
    override fun update(json: JsonObject) {
        if (!Objects.equals(Looper.myLooper(), Looper.getMainLooper())) {
            EventHandler.post { update(json) }
            return
        }

        for (entry in json.entrySet())
            when (entry.key) {
                "icon", "label" -> update(entry.value.asJsonObject)
                "background"    -> fill = ColorUtils.parseJson(entry.value)
                "foreground"    -> fontColor = ColorUtils.parseJson(entry.value)
                "border"        -> border = ColorUtils.parseJson(entry.value)
                "font"          -> font(entry.value.asJsonObject)
                "text"          -> label = entry.value.asString
                "task"          -> task = Task.fromJson(entry.value)
            }
    }
}