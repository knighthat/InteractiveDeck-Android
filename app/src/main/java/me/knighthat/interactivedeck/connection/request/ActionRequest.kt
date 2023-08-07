package me.knighthat.interactivedeck.connection.request

import me.knighthat.interactivedeck.component.action.Action

class ActionRequest(action: Action) : Request(RequestType.ACTION, action.json())