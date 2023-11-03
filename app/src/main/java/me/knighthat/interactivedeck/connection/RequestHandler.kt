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

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.knighthat.interactivedeck.component.ibutton.IButton
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

    private fun addButtons(array: JsonArray) {
        for (button in array) {
            if (button !is JsonObject || !button.has("profile")) continue
            val profile = UUID.fromString(button["profile"].asString) ?: continue

            Persistent
                .findProfile(profile)
                .ifPresent {
                    it.addButton(IButton.fromJson(profile, button.asJsonObject))
                }
        }
    }

    private fun addProfiles(array: JsonArray) {
        array
            .map(JsonElement::getAsJsonObject)
            .forEach { Persistent.add(Profile.fromJson(it)) }
    }

    private fun <T> getFunction(target: TargetedRequest.Target, type: T, buttonFunc: (T) -> Unit, profileFunc: (T) -> Unit) {
        return when (target) {
            TargetedRequest.Target.PROFILE -> profileFunc
            TargetedRequest.Target.BUTTON  -> buttonFunc
        }(type)
    }

    override fun handleActionRequest(request: ActionRequest) {}

    override fun handleAddRequest(request: AddRequest) = getFunction(request.target, request.payload, this::addButtons, this::addProfiles)

    override fun handlePairRequest(request: Request) {
        AddRequest(
            TargetedRequest.Target.PROFILE,
            request.payload.asJsonArray
        ).send()
    }

    override fun handleRemoveRequest(request: RemoveRequest) {
        request
            .payload
            .map(JsonElement::getAsString)
            .map(UUID::fromString)
            .forEach {
                when (request.target) {
                    TargetedRequest.Target.PROFILE -> Persistent::findProfile
                    TargetedRequest.Target.BUTTON  -> Persistent::findButton
                }(it).ifPresent(Removable::remove)
            }
    }

    override fun handleUpdateRequest(request: UpdateRequest) {
        when (request.target) {
            TargetedRequest.Target.PROFILE -> Persistent::findProfile
            TargetedRequest.Target.BUTTON  -> Persistent::findButton
        }(request.uuid).ifPresent {
            it.update(request.payload.asJsonObject)
        }
    }
}