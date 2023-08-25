package me.knighthat.interactivedeck.task

import java.util.UUID

class GotoPage(uuid: UUID) : Task {

    val uuid: UUID

    init {
        this.uuid = uuid
    }
}