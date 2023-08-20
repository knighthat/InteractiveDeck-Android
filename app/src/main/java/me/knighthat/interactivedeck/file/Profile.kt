package me.knighthat.interactivedeck.file

import com.google.gson.JsonObject
import me.knighthat.interactivedeck.component.ibutton.IButton
import me.knighthat.interactivedeck.exception.ProfileFormatException
import me.knighthat.interactivedeck.json.Json
import me.knighthat.interactivedeck.json.JsonSerializable
import java.util.UUID


class Profile(
    private val uuid: UUID,
    var displayName: String,
    var isDefault: Boolean,
    private var columns: Int,
    private var rows: Int,
    private var gap: Int,
    private val buttons: MutableList<IButton>
) : JsonSerializable {

    companion object {
        fun fromJson(json: JsonObject): Profile {
            if (!json.has("uuid") ||
                !json.has("displayName") ||
                !json.has("default") ||
                !json.has("rows") ||
                !json.has("columns") ||
                !json.has("gap") ||
                !json.has("buttons")
            )
                throw ProfileFormatException("Missing information")

            val idStr = json["uuid"].asString
            val uuid = UUID.fromString(idStr)
            val displayName = json["displayName"].asString
            val isDefault = json["default"].asBoolean
            val rows = json["rows"].asInt
            val columns = json["columns"].asInt
            val gap = json["gap"].asInt
            val buttons = ArrayList<IButton>()
            val btnJson = json.getAsJsonArray("buttons")
            btnJson.forEach {
                val btn = IButton(it.asJsonObject)
                buttons.add(btn)
            }

            return Profile(uuid, displayName, isDefault, columns, rows, gap, buttons)
        }
    }

    fun uuid(): UUID {
        return uuid
    }

    fun displayName(): String {
        return displayName
    }

    fun columns(): Int {
        return columns
    }

    fun rows(): Int {
        return rows
    }

    fun gap(): Int {
        return gap
    }

    fun buttons(): MutableList<IButton> {
        return this.buttons
    }

    override fun serialize(): JsonObject {
        /* Template
         * {
         *      "uuid": $uuid,
         *      "displayName": $displayName,
         *      "default": $isDefault,
         *      "rows": $rows,
         *      "columns": $columns,
         *      "gap": $gap,
         *      "buttons":
         *      [
         *          buttons
         *      ]
         * }
         */
        val json = JsonObject()

        json.add("uuid", Json.parse(uuid()))
        json.add("displayName", Json.parse(displayName()))
        json.add("default", Json.parse(isDefault))
        json.add("rows", Json.parse(rows()))
        json.add("columns", Json.parse(columns()))
        json.add("gap", Json.parse(gap()))
        json.add("buttons", Json.parse(buttons()))

        return json
    }
}