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

import androidx.annotation.MainThread
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.component.ibutton.IButton
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.vars.Memory
import java.util.UUID

data class Profile(
    val uuid: UUID,
    var displayName: String,
    val isDefault: Boolean,
    private val buttons: MutableList<IButton>,
    private var columns: Int,
    private var rows: Int,
    private var gap: Int
) {
    companion object {
        fun fromJson(json: JsonObject): Profile {
            val idStr = json["uuid"].asString
            val isDefault = json["default"].asBoolean

            val profile = Profile(
                UUID.fromString(idStr),
                "",
                isDefault,
                ArrayList(),
                1,
                1,
                0,
            )
            profile.update(json)

            return profile
        }
    }

    fun columns(): Int = this.columns

    fun rows(): Int = this.rows

    fun gap(): Int = this.gap

    @Synchronized
    fun buttons() = this.buttons

    fun update(json: JsonObject) = EventHandler.post { update0(json) }

    /*
     * There is a limit of how many buttons can be displayed at once
     * and it depends on how big (in resolution) your screen is.<br>
     *
     * The smallest at the time of writing is 176w x 96h.<br>
     *
     * An easy way to get the number of buttons can be showed is:
     * - Width: 176 * columns() <= screen's width
     * - Height 96 * rows() <= screen's height
     *
     * If the number exceed, only first column or row will be showed
     */
    @MainThread
    private fun update0(json: JsonObject) {
        if (json.has("displayName"))
            this.displayName = json["displayName"].asString

        if (json.has("columns"))
            this.columns = json["columns"].asInt

        if (json.has("rows"))
            this.rows = json["rows"].asInt

        if (json.has("gap"))
            this.gap = json["gap"].asInt

        if (json.has("buttons"))
            addButtons(json["buttons"].asJsonArray)

        if (Memory.active == this)
            Memory.active = this
    }

    fun addButtons(array: JsonArray) {
        array.forEach {
            val json = it.asJsonObject
            val button = IButton.fromJson(json)

            buttons().add(button)
            Memory.add(button)
        }
    }

    fun removeButtons(array: JsonArray) {
        val toBeDeleted = ArrayList<UUID>()
        array.forEach {
            val uuid = UUID.fromString(it.asString)
            toBeDeleted.add(uuid)
        }
        val buttons = buttons().iterator()
        while (buttons.hasNext()) {
            val button = buttons.next()
            if (!toBeDeleted.contains(button.uuid))
                continue
            buttons.remove()
            Memory.remove(button)
        }
    }
}
