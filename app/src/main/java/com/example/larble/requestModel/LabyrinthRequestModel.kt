package com.example.larble.requestModel

import com.example.larble.Cell

data class LabyrinthRequestModel(
    val token: String,
    val labyrinth: Array<Array<Cell>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LabyrinthRequestModel

        if (token != other.token) return false
        if (!labyrinth.contentDeepEquals(other.labyrinth)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token.hashCode()
        result = 31 * result + labyrinth.contentDeepHashCode()
        return result
    }
}
