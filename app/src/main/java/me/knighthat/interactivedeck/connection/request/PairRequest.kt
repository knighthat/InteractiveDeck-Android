package me.knighthat.interactivedeck.connection.request

import me.knighthat.interactivedeck.connection.DeviceInfo

class PairRequest : Request(type = RequestType.PAIR, content = DeviceInfo().serialize())