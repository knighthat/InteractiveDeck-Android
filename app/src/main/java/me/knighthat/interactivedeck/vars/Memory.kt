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
import me.knighthat.interactivedeck.component.ibutton.IButton
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.file.Profile
import java.util.Optional
import java.util.UUID

class Memory {

    companion object {
        @Volatile
        lateinit var activeProfile: ActiveProfile

        val aLive: LiveData<Profile> get() = activeProfile.active
        var active: Profile?
            get() = aLive.value
            set(value) {
                EventHandler.post {
                    activeProfile.active.value = value
                }
            }

        @Volatile
        private lateinit var _default: Profile

        private val profiles = ArrayList<Profile>()

        @Synchronized
        fun profiles() = profiles

        @Synchronized
        fun getProfile(uuid: UUID): Optional<Profile> {
            var profile: Profile? = null
            for (p in profiles())
                if (uuid == p.uuid) {
                    profile = p
                    break
                }
            return Optional.ofNullable(profile)
        }

        @Synchronized
        fun add(profile: Profile) {
            profiles().add(profile)
            profile.buttons.forEach(Memory::add)
            if (profile.isDefault) {
                _default = profile
                active = profile
            }
        }

        @Synchronized
        fun remove(profile: Profile): Boolean {
            if (active == profile)
                active = _default
            return profiles().remove(profile)
        }

        private val buttons = ArrayList<IButton>()

        @Synchronized
        fun buttons(): MutableList<IButton> = buttons

        @Synchronized
        fun getButton(uuid: UUID): Optional<IButton> {
            var button: IButton? = null
            for (b in buttons())
                if (uuid == b.uuid) {
                    button = b
                    break
                }
            return Optional.ofNullable(button)
        }

        @Synchronized
        fun add(button: IButton) = buttons().add(button)

        @Synchronized
        fun remove(button: IButton): Boolean = buttons().remove(button)
    }
}

class ActiveProfile(val active: MutableLiveData<Profile> = MutableLiveData()) : ViewModel()