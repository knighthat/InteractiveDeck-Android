package me.knighthat.interactivedeck.component.ibutton

import android.content.Context
import android.widget.GridLayout
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.JsonObject
import java.util.UUID


class IButton(context: Context, id: String) : AppCompatButton(context) {

    private val uuid: UUID
    private val icon = BIcon()
    private val label = BLabel()

    init {
        uuid = UUID.fromString(id)

        val params = GridLayout.LayoutParams()
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)

        this.layoutParams = params
    }

    constructor(context: Context, btnJson: JsonObject) : this(context, btnJson["uuid"].asString) {
        update(btnJson)
    }

    fun uuid(): UUID {
        return this.uuid
    }

    fun update(json: JsonObject) {
        val iconElement = json["icon"]
        if (iconElement != null)
            icon.update(iconElement.asJsonObject)

        val labelElement = json["label"]
        if (labelElement != null)
            label.update(labelElement.asJsonObject)

        setBackgroundColor(icon.inner)
        setTextColor(label.color)
        text = label.text
    }

    override fun toString(): String {
        return java.lang.String.format("IButton{id=%s,icon=%s,label=%s}", uuid, icon, label)
    }
}