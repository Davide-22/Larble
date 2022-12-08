package com.example.larble

data class PositionRequestModel(
    val token: String,
    val game_code: Int,
    val x: Float,
    val y: Float
)
