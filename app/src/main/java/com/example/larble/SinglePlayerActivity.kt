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
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SinglePlayerActivity : AppCompatActivity() {
    private lateinit var sh: SharedPreferences
    private lateinit var token: String
    private lateinit var easy: Button
    private lateinit var medium: Button
    private lateinit var hard: Button
    private lateinit var buttons: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleplayer)
        easy = findViewById(R.id.easy)
        medium = findViewById(R.id.medium)
        hard = findViewById(R.id.hard)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh.getString("token", "").toString()
        buttons = sh.getString("difficulty", "").toString()
        if(buttons == ""){
            val myEdit = sh.edit()
            myEdit.putString("difficulty", "100")
            myEdit.apply()
        }
        if(buttons == "110"){
            medium.isEnabled=true
            medium.isClickable=true
        }else if(buttons == "111"){
            medium.isEnabled=true
            medium.isClickable=true
            hard.isClickable=true
            hard.isEnabled=true
        }

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

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent = Intent(this@SinglePlayerActivity, MenuActivity::class.java)
                startActivity(intent)
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
}