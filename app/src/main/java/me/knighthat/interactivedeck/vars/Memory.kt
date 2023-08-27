/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use,copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.interactivedeck.vars

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.knighthat.interactivedeck.component.ibutton.IButton
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.file.Profile
import java.util.UUID

class Memory : ViewModel() {

    companion object {
        private val _internal = ViewModelProvider(EventHandler.DEF_ACTIVITY)[Memory::class.java]

        val list: MutableList<Profile> get() = _internal._profiles

        fun add(profile: Profile) {
            list.add(profile)
            profile.buttons.forEach(buttons::add)
        }

        fun getProfile(uuid: UUID): Profile? {
            list.forEach {
                if (it.uuid.equals(uuid))
                    return it
            }
            return null
        }

        val aLive: LiveData<Profile> get() = _internal._active
        var active: Profile?
            get() = aLive.value
            set(value) {
                EventHandler.post {
                    _internal._active.value = value
                }
            }

        val buttons: MutableList<IButton> get() = _internal._buttons

        fun getButton(uuid: UUID): IButton? {
            buttons.forEach {
                if (it.uuid.equals(uuid))
                    return it
            }
            return null
        }
    }

    private val _active = MutableLiveData<Profile>()
    private val _profiles = ArrayList<Profile>()
    private val _buttons = ArrayList<IButton>()
}