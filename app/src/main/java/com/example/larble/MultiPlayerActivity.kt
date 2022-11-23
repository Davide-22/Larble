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

class MultiPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        val newGame: Button = findViewById(R.id.new_game)
        val existedGame: Button = findViewById(R.id.existed_game)


        newGame.setOnClickListener {
            intent = Intent(this, NewGameActivity::class.java)
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val token: String = sh.getString("token", "").toString()
            val requestModel = TokenRequestModel(token)

            val response = ServiceBuilder.buildService(APIInterface::class.java)
            response.create_multiplayer_game(requestModel).enqueue(
                object: Callback<ResponseClass> {
                    override fun onResponse(
                        call: Call<ResponseClass>,
                        response: Response<ResponseClass>
                    ){
                        if(response.body()!!.status=="true"){
                            intent.putExtra("number", response.body()!!.msg)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@MultiPlayerActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                        Toast.makeText(this@MultiPlayerActivity, t.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            )
        }

        existedGame.setOnClickListener {
            intent = Intent(this, ExistedGameActivity::class.java)
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
                                    Toast.makeText(this@MultiPlayerActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                                }else{
                                    intent = Intent(this@MultiPlayerActivity, AccountActivity::class.java)
                                    intent.putExtra("email", response.body()!!.email)
                                    intent.putExtra("wins", response.body()!!.wins.toString())
                                    intent.putExtra("total_games", response.body()!!.total_games.toString())
                                    intent.putExtra("score", response.body()!!.score.toString())
                                    intent.putExtra("profile_picture", response.body()!!.profile_picture)
                                    startActivity(intent)
                                }
                            }
                            override fun onFailure(call: Call<PlayerClass>, t: Throwable) {
                                Toast.makeText(this@MultiPlayerActivity, t.toString(), Toast.LENGTH_LONG)
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