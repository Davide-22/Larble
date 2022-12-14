package com.example.larble.responseModel

data class PositionResponseClass(
    val status: String,
    val msg: String,
    val x: Float,
    val y: Float,
    val win: Boolean
)
