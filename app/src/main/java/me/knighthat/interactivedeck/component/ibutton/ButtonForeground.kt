/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use,copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.interactivedeck.component.ibutton

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.utils.ColorUtils
import me.knighthat.lib.component.RealtimeProperty
import org.intellij.lang.annotations.MagicConstant

@SuppressLint("ViewConstructor")
internal class ButtonForeground(
    private val owner: IButton
) : AppCompatTextView(owner.context), RealtimeProperty {

    var label: CharSequence
        get() = text
        set(value) {
            // If text does not change, then do nothing
            if (label == value) return

            owner.logUpdate("text", label, value)
            text = value
        }

    var weight: Int
        get() = typeface.style
        @MagicConstant(valuesFromClass = Typeface::class)
        set(value) {
            // If new weight equals current one, then do nothing
            if (weight == value) return

            owner.logUpdate("weight", weight, value)
            setTypeface(null, value)
        }

    var size: Float
        get() = textSize
        set(value) {
            // If new size is the same as old size, then do nothing
            if (size == value) return

            owner.logUpdate("size", size, value)
            textSize = value
        }

    var fontColor: Int
        get() = currentTextColor
        set(value) {
            // If new color is indifferent, then do nothing
            if (fontColor == value) return

            owner.logUpdate("foreground", fontColor, value)
            setTextColor(value)
        }

    init {
        isAllCaps = false
        textAlignment = TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER
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

    override fun update(json: JsonObject) {

        for (entry in json.entrySet())
            when (entry.key) {
                "foreground" -> fontColor = ColorUtils.parseJson(entry.value)
                "font"       -> font(entry.value.asJsonObject)
                "text"       -> label = entry.value.asString
            }
    }
}