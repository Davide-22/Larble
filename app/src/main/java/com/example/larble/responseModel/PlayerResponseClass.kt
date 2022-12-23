package com.example.larble.responseModel

data class PlayerResponseClass(
    val status: String,
    val msg: String,
    val email: String,
    val wins: Int,
    val total_games: Int,
    val score: Int,
    val profile_picture: String?
) : java.io.Serializable
