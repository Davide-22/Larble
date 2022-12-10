package com.example.larble.responseModel

data class LeaderboardResponseClass(
    val status: String,
    val msg: String,
    val leaderboard: ArrayList<LeaderboardClass>
)
