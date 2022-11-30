package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SinglePlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleplayer)
        val easy: Button = findViewById(R.id.easy)
        val medium: Button = findViewById(R.id.medium)
        val hard: Button = findViewById(R.id.hard)

        easy.setOnClickListener {
            intent = Intent(this, BallActivity::class.java)
            startActivity(intent)
        }
        medium.setOnClickListener {
            intent = Intent(this, BallActivity::class.java)
            startActivity(intent)
        }
        hard.setOnClickListener {
            intent = Intent(this, BallActivity::class.java)
            startActivity(intent)
        }

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
                                    Toast.makeText(this@SinglePlayerActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                                }else{
                                    intent = Intent(this@SinglePlayerActivity, AccountActivity::class.java)
                                    intent.putExtra("email", response.body()!!.email)
                                    intent.putExtra("wins", response.body()!!.wins.toString())
                                    intent.putExtra("total_games", response.body()!!.total_games.toString())
                                    intent.putExtra("score", response.body()!!.score.toString())
                                    intent.putExtra("profile_picture", response.body()!!.profile_picture)
                                    startActivity(intent)
                                }
                            }
                            override fun onFailure(call: Call<PlayerClass>, t: Throwable) {
                                Toast.makeText(this@SinglePlayerActivity, t.toString(), Toast.LENGTH_LONG)
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
        intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}