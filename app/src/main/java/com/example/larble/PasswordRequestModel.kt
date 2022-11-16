package com.example.larble

data class PasswordRequestModel(
    val token: String,
    val oldpassword: String,
    val password: String
)
