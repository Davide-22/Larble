package com.example.larble.requestModel

data class PasswordRequestModel(
    val token: String,
    val oldpassword: String,
    val password: String
)
