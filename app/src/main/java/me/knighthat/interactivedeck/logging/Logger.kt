/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use,copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.interactivedeck.logging

import android.widget.Toast
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.lib.logging.Log
import me.knighthat.lib.logging.Logger
import java.util.concurrent.TimeUnit

class Logger : Logger {

    private fun toast(message: String, duration: Int = Toast.LENGTH_LONG) {
        EventHandler.post {
            Toast.makeText(EventHandler.DEF_ACTIVITY, message, duration).show()
        }
    }

    override fun issueWebsite(): String = "https://github.com/knighthat/InteractiveDeck-Android/issues"

    override fun log(level: Log.LogLevel, s: String, skipQueue: Boolean): Runnable {
        return Runnable {

            val threadName = Thread.currentThread().name

            when (level) {
                Log.LogLevel.DEBUG -> android.util.Log.d(threadName, s)

                Log.LogLevel.INFO -> {
                    android.util.Log.i(threadName, s)
                    toast(s, Toast.LENGTH_SHORT)
                }

                Log.LogLevel.WARNING -> {
                    android.util.Log.w(threadName, s)
                    toast(s)
                }

                Log.LogLevel.ERROR -> {
                    android.util.Log.e(threadName, s)
                    toast(s)
                }
            }
        }
    }

    override fun sysSkipQueue(): Boolean = false

    override fun timeUnit(): TimeUnit = TimeUnit.SECONDS

    override fun waitTime(): Long = 5
}