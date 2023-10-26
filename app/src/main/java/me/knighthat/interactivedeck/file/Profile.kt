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
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.component.ibutton.IButton
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.vars.Memory
import me.knighthat.lib.exception.ProfileFormatException
import me.knighthat.lib.json.JsonArrayConverter
import me.knighthat.lib.profile.AbstractProfile
import java.util.UUID

class Profile(
    override val uuid: UUID,
    displayName: String,
    isDefault: Boolean
) : AbstractProfile<IButton>(isDefault, ArrayList(), displayName, 4, 2, 3) {

    companion object {
        @Throws(ProfileFormatException::class)
        fun fromJson(json: JsonObject): Profile {
            if (!json.has("uuid"))
                throw ProfileFormatException("Missing UUID!")
            if (!json.has("default"))
                throw ProfileFormatException("Cannot decide whether profile is default!")

            val idStr = json["uuid"].asString
            val isDefault = json["default"].asBoolean

            val profile = Profile(
                UUID.fromString(idStr),
                "",
                isDefault,
            )
            profile.update(json)

            return profile
        }
    }

    fun addButtons(array: JsonArray) {
        array.forEach {
            val button = IButton.fromJson(uuid, it.asJsonObject)

            buttons.add(button)
            Memory.add(button)
        }
    }

    fun removeButtons(array: JsonArray) {
        JsonArrayConverter
            .toStringArray(array)
            .forEach { uuidStr ->
                val uuid = UUID.fromString(uuidStr)
                if (buttons.removeIf { it.uuid == uuid })
                    Memory.getButton(uuid).ifPresent(IButton::remove)
            }
    }

    override fun remove() = Memory.remove(this)

    override fun updateButtons(buttonJson: JsonElement) {
        if (buttonJson.isJsonArray)
            addButtons(buttonJson.asJsonArray)
    }

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
    override fun update(json: JsonObject) {
        EventHandler.post {
            super.update(json)
            // Reload display panel
            if (Memory.active == this)
                Memory.active = this
        }
    }
}
