package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import com.example.larble.responseModel.LeaderboardResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {
    private lateinit var sh: SharedPreferences
    private lateinit var username: String
    private lateinit var token: String
    private lateinit var singlePlayer: Button
    private lateinit var multiPlayer : Button
    private lateinit var settings: ImageButton
    private lateinit var scoreboard: ImageButton
    private lateinit var text: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        singlePlayer = findViewById(R.id.single_player)
        multiPlayer = findViewById(R.id.multiplayer)
        settings = findViewById(R.id.settings)
        scoreboard = findViewById(R.id.scoreboard)

        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        username = sh.getString("username", "").toString()
        token = sh.getString("token", "").toString()

        text = findViewById(R.id.name)
        "Ciao $username".also { text.text = it }

        singlePlayer.setOnClickListener {
            intent = Intent(this, SinglePlayerActivity::class.java)
            startActivity(intent)
        }

        multiPlayer.setOnClickListener {
            intent = Intent(this, MultiPlayerActivity::class.java)
            startActivity(intent)
        }

        settings.setOnClickListener{
            intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        scoreboard.setOnClickListener{
            intent = Intent(this, LeaderboardActivity::class.java)
            val requestModel = TokenRequestModel(token)
            val response = ServiceBuilder.buildService(APIInterface::class.java)
            response.leaderboard(requestModel).enqueue(
                object: Callback<LeaderboardResponseClass> {
                    override fun onResponse(
                        call: Call<LeaderboardResponseClass>,
                        response: Response<LeaderboardResponseClass>
                    ){
                        if(response.body()== null){
                            Toast.makeText(this@MenuActivity, "Connection with the server failed", Toast.LENGTH_LONG)
                                .show()
                        }
                        else if(response.body()!!.status=="true"){
                            intent.putExtra("leaderboard", response.body()!!.leaderboard)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@MenuActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    override fun onFailure(call: Call<LeaderboardResponseClass>, t: Throwable) {
                        intent = Intent(this@MenuActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            )
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
                finish()
            }
        })


    }

    override fun onResume(){
        super.onResume()
        "Ciao $username".also { text.text = it }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                val requestModel = TokenRequestModel(token)

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.playerInfo(requestModel).enqueue(
                    object: Callback<PlayerResponseClass> {
                        override fun onResponse(
                            call: Call<PlayerResponseClass>,
                            response: Response<PlayerResponseClass>
                        ){
                            if(response.body()== null){
                                Toast.makeText(this@MenuActivity, "Connection with the server failed", Toast.LENGTH_LONG)
                                    .show()
                            }
                            else if(response.body()!!.status == "false"){
                                Toast.makeText(this@MenuActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                            }else{
                                intent = Intent(this@MenuActivity, AccountActivity::class.java)
                                intent.putExtra("account", response.body())
                                startActivity(intent)
                            }
                        }
                        override fun onFailure(call: Call<PlayerResponseClass>, t: Throwable) {
                            intent = Intent(this@MenuActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                )

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}