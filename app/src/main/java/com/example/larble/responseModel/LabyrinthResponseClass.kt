package com.example.larble.responseModel

import com.example.larble.Cell

data class LabyrinthResponseClass(
    val status: String,
    val msg: String,
    val labyrinth: Array<Array<Cell>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LabyrinthResponseClass

        if (status != other.status) return false
        if (msg != other.msg) return false
        if (!labyrinth.contentDeepEquals(other.labyrinth)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + msg.hashCode()
        result = 31 * result + labyrinth.contentDeepHashCode()
        return result
    }
}
