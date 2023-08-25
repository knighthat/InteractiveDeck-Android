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
            val profile = Profile(
                UUID.fromString(idStr),
                "",
                false,
                1,
                1,
                0,
                ArrayList()
            )
            profile.update(json)

            return profile
        }
    }

    fun update(json: JsonObject) {
        this.displayName = json["displayName"].asString
        this.isDefault = json["default"].asBoolean
        this.columns = json["columns"].asInt
        this.rows = json["rows"].asInt
        this.gap = json["gap"].asInt

        this.buttons.clear()
        json.getAsJsonArray("buttons").forEach {
            val btn = IButton(it.asJsonObject)
            this.buttons.add(btn)
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