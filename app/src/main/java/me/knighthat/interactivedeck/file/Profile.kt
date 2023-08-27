/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use,copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.interactivedeck.file

import com.google.gson.JsonObject
import me.knighthat.interactivedeck.component.ibutton.IButton
import me.knighthat.interactivedeck.exception.ProfileFormatException
import java.util.UUID

data class Profile(
    val uuid: UUID,
    var displayName: String,
    var isDefault: Boolean,
    private var columns: Int,
    private var rows: Int,
    private var gap: Int,
    val buttons: MutableList<IButton>
) {
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
            val btn = IButton.fromJson(it.asJsonObject)
            this.buttons.add(btn)
        }
    }
}
