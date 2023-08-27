package me.knighthat.interactivedeck.json

import com.google.gson.JsonElement

@FunctionalInterface
interface JsonSerializable {

    fun serialize(): JsonElement
}