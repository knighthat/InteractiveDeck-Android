package me.knighthat.interactivedeck.task

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.UUID

interface Task {

    companion object {
        fun fromJson(json: JsonElement): Task? {
            if (json.isJsonNull || json !is JsonObject) return null

            var task: Task? = null
            val type = json["action_type"].asString

            if (type == "SWITCH_PROFILE") {

                val uuid = UUID.fromString(json["profile"].asString)
                task = GotoPage(uuid)

            }

            return task
        }
    }
}