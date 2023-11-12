package me.knighthat.interactivedeck.component.ibutton

import android.annotation.SuppressLint
import android.os.Looper
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.persistent.Persistent
import me.knighthat.interactivedeck.task.GotoPage
import me.knighthat.interactivedeck.task.Task
import me.knighthat.lib.component.ibutton.InteractiveButton
import me.knighthat.lib.logging.Log
import java.util.Objects
import java.util.UUID


@SuppressLint("ViewConstructor")
class IButton(
    override val uuid: UUID,
    override val profile: UUID,
    override val posX: Int,
    override val posY: Int
) : ConstraintLayout(EventHandler.DEF_ACTIVITY), InteractiveButton {

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

    private val back: ButtonBackground = ButtonBackground(this)
    private val front: ButtonForeground = ButtonForeground(this)

    var task: Task? = null
        set(value) {
            // If new task equals current task, then do nothing
            if (Objects.equals(task, value))
                return

            logUpdate("task", task, value)
            field =
                    if (value is GotoPage)
                        GotoPage(value.uuid)
                    else
                        null
        }

    init {
        addView(back)
        addView(front)

        val imageParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        imageParams.topToTop = LayoutParams.PARENT_ID
        back.layoutParams = imageParams

        val textParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        textParams.topToBottom = back.id
        front.layoutParams = textParams
    }

    override fun remove() = Persistent.findProfile(profile).ifPresent { it.removeButton(this) }

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
                "icon"  -> back.update(entry.value.asJsonObject)
                "label" -> front.update(entry.value.asJsonObject)
                "task"  -> task = Task.fromJson(entry.value)
            }
    }
}
