package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import com.example.larble.responseModel.LeaderboardClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        val leaderboard: ArrayList<LeaderboardClass> = intent.getSerializableExtra("leaderboard") as ArrayList<LeaderboardClass>

        val username: TextView = findViewById(R.id.textView1)
        val wins: TextView = findViewById(R.id.textView16)
        username.text=leaderboard[0].username
        wins.text=leaderboard[0].wins.toString()
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
                        object: Callback<PlayerResponseClass> {
                            override fun onResponse(
                                call: Call<PlayerResponseClass>,
                                response: Response<PlayerResponseClass>
                            ){
                                if(response.body()!!.status == "false"){
                                    Toast.makeText(this@LeaderboardActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                                }else{
                                    intent = Intent(this@LeaderboardActivity, AccountActivity::class.java)
                                    intent.putExtra("email", response.body()!!.email)
                                    intent.putExtra("wins", response.body()!!.wins.toString())
                                    intent.putExtra("total_games", response.body()!!.total_games.toString())
                                    intent.putExtra("score", response.body()!!.score.toString())
                                    intent.putExtra("profile_picture", response.body()!!.profile_picture)
                                    startActivity(intent)
                                }
                            }
                            override fun onFailure(call: Call<PlayerResponseClass>, t: Throwable) {
                                Toast.makeText(this@LeaderboardActivity, t.toString(), Toast.LENGTH_LONG)
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

}