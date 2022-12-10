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
import androidx.appcompat.app.AppCompatActivity
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import com.example.larble.responseModel.LeaderboardResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {
    private var sh: SharedPreferences? = null
    private var username: String? = null
    private var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val singlePlayer: Button = findViewById(R.id.single_player)
        val multiPlayer: Button = findViewById(R.id.multiplayer)
        val settings: ImageButton = findViewById(R.id.settings)
        val scoreboard: ImageButton = findViewById(R.id.scoreboard)

        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        username = sh!!.getString("username", "")
        token = sh!!.getString("token", "")

        val text: TextView = findViewById(R.id.name)
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
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val token: String? = sh.getString("token", "")
            val requestModel = token?.let { TokenRequestModel(it) }

            val response = ServiceBuilder.buildService(APIInterface::class.java)
            if (requestModel != null) {
                response.leaderboard(requestModel).enqueue(
                    object: Callback<LeaderboardResponseClass> {
                        override fun onResponse(
                            call: Call<LeaderboardResponseClass>,
                            response: Response<LeaderboardResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                intent.putExtra("leaderboard", response.body()!!.leaderboard)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@MenuActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        override fun onFailure(call: Call<LeaderboardResponseClass>, t: Throwable) {
                            Toast.makeText(this@MenuActivity, t.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )
            }
        }

    }

    override fun onResume(){
        super.onResume()
        val text: TextView = findViewById(R.id.name)
        "Ciao $username".also { text.text = it }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                val requestModel = token?.let { TokenRequestModel(it) }

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                if (requestModel != null) {
                    response.playerInfo(requestModel).enqueue(
                        object: Callback<PlayerResponseClass> {
                            override fun onResponse(
                                call: Call<PlayerResponseClass>,
                                response: Response<PlayerResponseClass>
                            ){
                                if(response.body()!!.status == "false"){
                                    Toast.makeText(this@MenuActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                                }else{
                                    intent = Intent(this@MenuActivity, AccountActivity::class.java)
                                    intent.putExtra("account", response.body())
                                    startActivity(intent)
                                }
                            }
                            override fun onFailure(call: Call<PlayerResponseClass>, t: Throwable) {
                                Toast.makeText(this@MenuActivity, t.toString(), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    )
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAffinity()
        finish()
    }
}