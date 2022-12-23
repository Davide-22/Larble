package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import com.example.larble.responseModel.LeaderboardClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var sh :SharedPreferences
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh.getString("token", "").toString()

        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        val leaderboard: ArrayList<LeaderboardClass> = intent.getSerializableExtra("leaderboard") as ArrayList<LeaderboardClass>
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val data = ArrayList<ItemsLeaderboard>(15)

        for (i in 0 until leaderboard.size) {
            data.add(ItemsLeaderboard(leaderboard[i].profile_picture, leaderboard[i].username, leaderboard[i].wins.toString(), leaderboard[i].score.toString()))
        }

        val adapter = CustomAdapter(data)

        recyclerview.adapter = adapter
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
                            if(response.body()!!.status == "false"){
                                Toast.makeText(this@LeaderboardActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                            }else{
                                intent = Intent(this@LeaderboardActivity, AccountActivity::class.java)
                                intent.putExtra("account", response.body())
                                startActivity(intent)
                            }
                        }
                        override fun onFailure(call: Call<PlayerResponseClass>, t: Throwable) {
                            intent = Intent(this@LeaderboardActivity, MainActivity::class.java)
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