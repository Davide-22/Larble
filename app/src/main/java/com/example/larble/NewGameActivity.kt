package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewGameActivity : AppCompatActivity() {
    private var result = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)

        val game: Button = findViewById(R.id.play2)
        val text: TextView = findViewById(R.id.number)
        text.text = intent.getStringExtra("number")
        val requestModel = intent.getStringExtra("number")?.let { GameCodeModel(it) }
        val response = ServiceBuilder.buildService(APIInterface::class.java)

        game.setOnClickListener {
            intent = Intent(this, MultiPlayerGameActivity::class.java)

            if (requestModel != null) {
                while(result == ""){
                    response.checkForPlayer(requestModel).enqueue(
                        object: Callback<ResponseClass> {
                            override fun onResponse(
                                call: Call<ResponseClass>,
                                response: Response<ResponseClass>
                            ){
                                if(response.body()!!.status=="true"){
                                    result = response.body()!!.status
                                    startActivity(intent)
                                }
                            }

                            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                Toast.makeText(this@NewGameActivity, t.toString(), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    )
                    Thread.sleep(3000)
                }
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        result = "false"
    }
}