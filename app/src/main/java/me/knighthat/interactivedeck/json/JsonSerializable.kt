package me.knighthat.interactivedeck.json

import com.google.gson.JsonObject

interface JsonSerializable {

    fun serialize(): JsonObject
}