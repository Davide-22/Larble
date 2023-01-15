package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.larble.requestModel.LabyrinthRequestModel
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import com.example.larble.responseModel.ResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MultiPlayerActivity : AppCompatActivity() {
    private lateinit var sh :SharedPreferences
    private lateinit var token: String
    private lateinit var newGame: Button
    private lateinit var existedGame: Button
    private var clicked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        newGame = findViewById(R.id.new_game)
        existedGame = findViewById(R.id.existed_game)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh.getString("token", "").toString()

        newGame.setOnClickListener {
            if(!clicked){
                clicked = true
                intent = Intent(this, NewGameActivity::class.java)
                val mazeView = MazeView(this)
                val requestModel = LabyrinthRequestModel(token,mazeView.getCells())

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.createMultiplayerGame(requestModel).enqueue(
                    object: Callback<ResponseClass> {
                        override fun onResponse(
                            call: Call<ResponseClass>,
                            response: Response<ResponseClass>
                        ){
                            if(response.body()== null){
                                Toast.makeText(this@MultiPlayerActivity, "Connection with the server failed", Toast.LENGTH_LONG)
                                    .show()
                                intent = Intent(this@MultiPlayerActivity, MenuActivity::class.java)
                                startActivity(intent)
                                clicked = false
                            }
                            else if(response.body()!!.status=="true"){
                                intent.putExtra("number", response.body()!!.msg)
                                intent.putExtra("labyrinth", mazeView.getCells())
                                startActivity(intent)
                                //clicked = false
                            }else{
                                Toast.makeText(this@MultiPlayerActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                                clicked = false
                            }
                        }
                        override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                            intent = Intent(this@MultiPlayerActivity, MainActivity::class.java)
                            startActivity(intent)
                            clicked = false
                        }
                    }
                )
            }
        }

        existedGame.setOnClickListener {
            if(!clicked){
                clicked = true
                intent = Intent(this, ExistedGameActivity::class.java)
                startActivity(intent)
                clicked = false
            }
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent = Intent(this@MultiPlayerActivity, MenuActivity::class.java)
                startActivity(intent)
                clicked = false
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                if(!clicked){
                    clicked = true
                    val requestModel = TokenRequestModel(token)

                    val response = ServiceBuilder.buildService(APIInterface::class.java)
                    response.playerInfo(requestModel).enqueue(
                        object: Callback<PlayerResponseClass> {
                            override fun onResponse(
                                call: Call<PlayerResponseClass>,
                                response: Response<PlayerResponseClass>
                            ){
                                if(response.body()== null){
                                    Toast.makeText(this@MultiPlayerActivity, "Connection with the server failed", Toast.LENGTH_LONG)
                                        .show()
                                    intent = Intent(this@MultiPlayerActivity, MenuActivity::class.java)
                                    startActivity(intent)
                                    clicked = false
                                }
                                else if(response.body()!!.status == "false"){
                                    Toast.makeText(this@MultiPlayerActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                                    clicked = false
                                }else{
                                    intent = Intent(this@MultiPlayerActivity, AccountActivity::class.java)
                                    intent.putExtra("account", response.body())
                                    startActivity(intent)
                                    clicked = false
                                }
                            }
                            override fun onFailure(call: Call<PlayerResponseClass>, t: Throwable) {
                                intent = Intent(this@MultiPlayerActivity, MainActivity::class.java)
                                startActivity(intent)
                                clicked = false
                            }
                        }
                    )
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}