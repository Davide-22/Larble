package com.example.larble.responseModel

data class LeaderboardClass(
    val username: String,
    val wins: Int,
    val score: Int
) : java.io.Serializable
