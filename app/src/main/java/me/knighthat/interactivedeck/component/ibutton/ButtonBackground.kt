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
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatImageView
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.utils.ColorUtils
import me.knighthat.lib.component.RealtimeProperty
import me.knighthat.lib.json.JsonArrayConverter

@SuppressLint("ViewConstructor")
internal class ButtonBackground(
    private val owner: IButton
) : AppCompatImageView(owner.context), RealtimeProperty {

    var fill: Int = 0
        set(value) {
            // If new color is the same as the old one, then do nothing
            if (fill == value) return

            owner.logUpdate("background", fill, value)

            field = value
            repaint()
        }

    var border = 0
        set(value) {
            // If new color is the same as the old one, then do nothing
            if (border == value) return

            owner.logUpdate("border", border, value)

            field = value
            repaint()
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

    @MainThread
    override fun update(json: JsonObject) {
        for (entry in json.entrySet())
            when (entry.key) {
                "background" -> fill = ColorUtils.parseJson(entry.value)
                "border"     -> border = ColorUtils.parseJson(entry.value)
                "symbol"     -> {
                    val array = JsonArrayConverter.toByteArray(entry.value.asJsonArray)
                    val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                    setImageBitmap(bitmap)
                }
            }
    }
}