/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use,copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.interactivedeck.connection.request

import android.content.Intent
import com.google.gson.JsonArray
import me.knighthat.interactivedeck.activity.ButtonsLayout
import me.knighthat.interactivedeck.console.Log
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.file.Profile
import me.knighthat.interactivedeck.vars.Memory
import java.util.UUID

class RequestHandler {
    fun process(request: Request) {
        val content = request.content
        val type = request.type

        if (request !is TargetedRequest && type != Request.RequestType.PAIR) {
            Log.err("Invalid request format!", false)
            Log.err(request.toString(), false)
            return
        }

        when (type) {
            Request.RequestType.ADD -> {
                val tRequest = request as TargetedRequest
                val reContent = tRequest.content.asJsonArray
                if (tRequest.uuid == null)
                    addProfiles(reContent)
                else
                    addButtons(tRequest.uuid, reContent)
            }

            Request.RequestType.REMOVE -> {
                val tRequest = request as TargetedRequest
                val reContent = tRequest.content.asJsonArray
                if (tRequest.uuid == null)
                    removeProfiles(reContent)
                else
                    removeButtons(tRequest.uuid, reContent)
            }

            Request.RequestType.UPDATE -> {
                val tRequest = request as TargetedRequest
                val reContent = tRequest.content.asJsonObject

                if (tRequest.uuid == null)
                    return

                when (tRequest.target) {
                    TargetedRequest.Target.BUTTON ->
                        Memory.getButton(tRequest.uuid).ifPresent { it.update(reContent) }

                    TargetedRequest.Target.PROFILE ->
                        Memory.getProfile(tRequest.uuid).ifPresent { it.update(reContent) }
                }
            }

            Request.RequestType.PAIR -> pair(content.asJsonArray)
            else -> {}
        }
    }

    private fun pair(content: JsonArray) {
        val ids = JsonArray()
        content.forEach { ids.add(it.asString) }

        Request(Request.RequestType.ADD, ids).send()

        EventHandler.post {
            val intent = Intent(EventHandler.DEF_ACTIVITY, ButtonsLayout::class.java)
            EventHandler.DEF_ACTIVITY.startActivities(Array(1) { intent })
        }
    }

    private fun addButtons(pUuid: UUID, array: JsonArray) {
        Memory
            .getProfile(pUuid)
            .ifPresent { it.addButtons(array) }
    }

    private fun addProfiles(array: JsonArray) {
        array.forEach {
            val json = it.asJsonObject
            val profile = Profile.fromJson(json)
            Memory.add(profile)
        }
    }

    private fun removeButtons(pUuid: UUID, array: JsonArray) {
        Memory
            .getProfile(pUuid)
            .ifPresent { it.removeButtons(array) }
    }

    private fun removeProfiles(array: JsonArray) {
        for (it in array) {
            val idStr = it.asString

            Log.deb("Deleting: $idStr")

            Memory
                .getProfile(UUID.fromString(idStr))
                .ifPresent(Memory::remove)
        }
    }
}