/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use,copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.interactivedeck.connection

import android.content.Intent
import com.google.gson.JsonElement
import me.knighthat.interactivedeck.activity.ButtonsLayout
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.file.Profile
import me.knighthat.interactivedeck.persistent.Persistent
import me.knighthat.lib.component.Removable
import me.knighthat.lib.connection.request.AbstractRequestHandler
import me.knighthat.lib.connection.request.ActionRequest
import me.knighthat.lib.connection.request.AddRequest
import me.knighthat.lib.connection.request.RemoveRequest
import me.knighthat.lib.connection.request.Request
import me.knighthat.lib.connection.request.TargetedRequest
import me.knighthat.lib.connection.request.UpdateRequest
import java.util.UUID

class RequestHandler : AbstractRequestHandler() {

    override fun handleActionRequest(request: ActionRequest) {}

    override fun handleAddRequest(request: AddRequest) {
        val array = request.payload.asJsonArray

        if (request.target == TargetedRequest.Target.BUTTON) {
            val uuid = request.uuid ?: return
            Persistent.findProfile(uuid).ifPresent { it.addButtons(array) }
        } else
            array.map(JsonElement::getAsJsonObject).forEach {
                val profile = Profile.fromJson(it)
                Persistent.add(profile)
                if (profile.isDefault)
                    Persistent.setDefaultProfile(profile)
            }
    }

    override fun handlePairRequest(request: Request) {
        AddRequest {
            it.addAll(request.payload.asJsonArray)
        }.send()

        EventHandler.post {
            val intent = Intent(EventHandler.DEF_ACTIVITY, ButtonsLayout::class.java)
            EventHandler.DEF_ACTIVITY.startActivity(intent)
        }
    }

    override fun handleRemoveRequest(request: RemoveRequest) {
        request
            .payload
            .asJsonArray
            .map(JsonElement::getAsString)
            .map(UUID::fromString)
            .forEach {
                when (request.target) {
                    TargetedRequest.Target.BUTTON  -> Persistent.findButton(it)
                    TargetedRequest.Target.PROFILE -> Persistent.findProfile(it)
                }.ifPresent(Removable::remove)
            }
    }

    override fun handleUpdateRequest(request: UpdateRequest) {
        val uuid = request.uuid ?: return

        when (request.target) {
            TargetedRequest.Target.BUTTON  -> Persistent.findButton(uuid)
            TargetedRequest.Target.PROFILE -> Persistent.findProfile(uuid)
        }.ifPresent { it.update(request.payload.asJsonObject) }
    }
}