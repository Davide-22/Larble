package com.example.larble

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIInterface {

    @POST("/signup")
    fun requestSignUp(@Body requestModel: SignUpRequestModel): Call<ResponseClass>

    @POST("/login")
    fun requestLogin(@Body requestModel: LoginRequestModel): Call<LoginResponseClass>

    @POST("/verify")
    fun verify(@Body requestModel: VerifyRequestModel): Call<ResponseClass>
}