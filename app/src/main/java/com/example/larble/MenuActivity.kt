package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val singlePlayer: Button = findViewById(R.id.single_player)
        val multiPlayer: Button = findViewById(R.id.multiplayer)
        val settings: ImageButton = findViewById(R.id.settings)
        val scoreboard: ImageButton = findViewById(R.id.scoreboard)

        val text: TextView = findViewById(R.id.name)
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val username: String? = sh.getString("username", "")
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
            intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume(){
        super.onResume()
        val text: TextView = findViewById(R.id.name)
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val username: String? = sh.getString("username", "")
        "Ciao $username".also { text.text = it }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                val token: String? = sh.getString("token", "")
                val requestModel = token?.let { TokenRequestModel(it) }

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                if (requestModel != null) {
                    response.playerInfo(requestModel).enqueue(
                        object: Callback<PlayerClass> {
                            override fun onResponse(
                                call: Call<PlayerClass>,
                                response: Response<PlayerClass>
                            ){
                                if(response.body()!!.status == "false"){
                                    Toast.makeText(this@MenuActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                                }else{
                                    intent = Intent(this@MenuActivity, AccountActivity::class.java)
                                    intent.putExtra("email", response.body()!!.email)
                                    intent.putExtra("wins", response.body()!!.wins.toString())
                                    intent.putExtra("total_games", response.body()!!.total_games.toString())
                                    intent.putExtra("score", response.body()!!.score.toString())
                                    intent.putExtra("profile_picture", response.body()!!.profile_picture)
                                    startActivity(intent)
                                }
                            }
                            override fun onFailure(call: Call<PlayerClass>, t: Throwable) {
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