package com.example.larble

import com.example.larble.requestModel.*
import com.example.larble.responseModel.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIInterface {

    @POST("/signup")
    fun requestSignUp(@Body requestModel: SignUpRequestModel): Call<ResponseClass>

    @POST("/login")
    fun requestLogin(@Body requestModel: LoginRequestModel): Call<LoginResponseClass>

    @POST("/verify")
    fun verify(@Body requestModel: TokenRequestModel): Call<ResponseClass>

    @POST("/create_multiplayer_game")
    fun createMultiplayerGame(@Body requestModel: LabyrinthRequestModel): Call<ResponseClass>

    @POST("/check_for_player2")
    fun checkForPlayer(@Body requestModel: GameCodeModel): Call<ResponseClass>

    @POST("/join_game")
    fun joinGame(@Body requestModel: GameCodeRequestModel): Call<LabyrinthResponseClass>

    @POST("/delete_game")
    fun deleteGame(@Body requestModel: GameCodeRequestModel): Call<ResponseClass>

    @POST("/change_password")
    fun changePassword(@Body requestModel: PasswordRequestModel): Call<ResponseClass>

    @POST("/change_username")
    fun changeUsername(@Body requestModel: UsernameRequestModel): Call<ResponseClass>

    @POST("/player_info")
    fun playerInfo(@Body requestModel: TokenRequestModel): Call<PlayerResponseClass>

    @POST("/change_profile_picture")
    fun insertPicture(@Body requestModel: ProfileRequestModel): Call<ResponseClass>

    @POST("/handle_multiplayer_game")
    fun takePosition(@Body requestModel: PositionRequestModel): Call<PositionResponseClass>

    @POST("/get_leaderboard")
    fun leaderboard(@Body requestModel: TokenRequestModel): Call<LeaderboardResponseClass>

    @POST("/winning_game")
    fun winning(@Body requestModel: GameCodeRequestModel): Call<ResponseClass>

    @POST("/delete_finished_game")
    fun deleteFinishedGame(@Body requestModel: GameCodeRequestModel): Call<ResponseClass>

    @POST("/google_login")
    fun loginGoogle(@Body requestModel: GoogleRequestModel): Call<ResponseClass>
}