package com.example.larble

data class PasswordRequestModel(
    val token: String,
    val `old-password`: String,
    val password: String
)
