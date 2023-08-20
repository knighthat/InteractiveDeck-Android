package me.knighthat.interactivedeck.button

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.knighthat.interactivedeck.component.ibutton.IButton

class Buttons : ViewModel() {

    private val _buttons = MutableLiveData<MutableList<IButton>>()
    val buttons: LiveData<MutableList<IButton>> get() = _buttons

    fun set(buttons: MutableList<IButton>) {
        _buttons.value = buttons
    }
}