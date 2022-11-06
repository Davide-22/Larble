package com.example.larble

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIInterface {

    @POST("/signup")
    fun requestSignUp(@Body requestModel: SignUpRequestModel): Call<SignUpResponseClass>

    @POST("/login")
    fun requestLogin(@Body requestModel: LoginRequestModel): Call<LoginResponseClass>
}