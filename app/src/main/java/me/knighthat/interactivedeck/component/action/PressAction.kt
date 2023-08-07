package me.knighthat.interactivedeck.component.action

import me.knighthat.interactivedeck.component.ibutton.IButton

class PressAction(button: IButton) : Action(type = ActionType.PRESS, button)