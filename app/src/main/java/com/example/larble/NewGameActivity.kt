package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewGameActivity : AppCompatActivity() {
    private var result = false
    private var code = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)

        val text: TextView = findViewById(R.id.number)
        text.text = intent.getStringExtra("number")
        code = text.text.toString().toInt()
        val requestModel = intent.getStringExtra("number")?.let { GameCodeModel(it) }

        intent = Intent(this, MultiPlayerGameActivity::class.java)
        val service = ServiceBuilder.buildService(APIInterface::class.java)

        Thread {
            while(!result) {
                try {
                    val callSync = requestModel?.let { it1 -> service.checkForPlayer(it1) }
                    val response = callSync?.execute()
                    if (response != null) {
                        if (response.body()!!.status == "true") {
                            intent.putExtra("number", code.toString())
                            startActivity(intent)
                            result = true
                        }
                    }
                } catch (ex: Exception) {
                    Log.d("error", Log.getStackTraceString(ex))
                }
                Thread.sleep(2000)
            }
        }.start()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        result = true
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token: String = sh.getString("token", "").toString()
        val requestModel = GameCodeRequestModel(code,token)

        val response = ServiceBuilder.buildService(APIInterface::class.java)
        response.deleteGame(requestModel).enqueue(
            object: Callback<ResponseClass> {
                override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                    Toast.makeText(this@NewGameActivity, t.toString(), Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(
                    call: Call<ResponseClass>,
                    response: Response<ResponseClass>
                ) {
                }
            }
        )
        intent = Intent(this, MultiPlayerActivity::class.java)
        startActivity(intent)
    }
}