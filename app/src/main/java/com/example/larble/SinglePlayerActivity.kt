package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SinglePlayerActivity : AppCompatActivity() {
    private lateinit var sh: SharedPreferences
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleplayer)
        val easy: Button = findViewById(R.id.easy)
        val medium: Button = findViewById(R.id.medium)
        val hard: Button = findViewById(R.id.hard)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh.getString("token", "").toString()

        easy.setOnClickListener {
            intent = Intent(this, BallActivity::class.java)
            intent.putExtra("difficulty","easy")
            startActivity(intent)
        }
        medium.setOnClickListener {
            intent = Intent(this, BallActivity::class.java)
            intent.putExtra("difficulty","medium")
            startActivity(intent)
        }
        hard.setOnClickListener {
            intent = Intent(this, BallActivity::class.java)
            intent.putExtra("difficulty","hard")
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
                val requestModel = TokenRequestModel(token)

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.playerInfo(requestModel).enqueue(
                    object: Callback<PlayerResponseClass> {
                        override fun onResponse(
                            call: Call<PlayerResponseClass>,
                            response: Response<PlayerResponseClass>
                        ){
                            if(response.body()!!.status == "false"){
                                Toast.makeText(this@SinglePlayerActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                            }else{
                                intent = Intent(this@SinglePlayerActivity, AccountActivity::class.java)
                                intent.putExtra("account", response.body())
                                startActivity(intent)
                            }
                        }
                        override fun onFailure(call: Call<PlayerResponseClass>, t: Throwable) {
                            intent = Intent(this@SinglePlayerActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                )

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