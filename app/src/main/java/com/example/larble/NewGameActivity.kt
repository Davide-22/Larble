package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.larble.requestModel.GameCodeModel
import com.example.larble.requestModel.GameCodeRequestModel
import com.example.larble.responseModel.ResponseClass
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewGameActivity : AppCompatActivity() {
    private var result = false
    private var code = 0
    private var job: Job = Job()
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)

        val text: TextView = findViewById(R.id.number)
        text.text = intent.getStringExtra("number")
        code = text.text.toString().toInt()
        val requestModel = intent.getStringExtra("number")?.let { GameCodeModel(it) }

        intent = Intent(this, MultiPlayerGameActivity::class.java)
        val service = ServiceBuilder.buildService(APIInterface::class.java)

        job = GlobalScope.launch {
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
                delay(1000)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        result = true
        job.cancel()
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token: String = sh.getString("token", "").toString()
        val requestModel = GameCodeRequestModel(code,token)

        val response = ServiceBuilder.buildService(APIInterface::class.java)
        response.deleteGame(requestModel).enqueue(
            object: Callback<ResponseClass> {
                override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                    intent = Intent(this@NewGameActivity, MainActivity::class.java)
                    startActivity(intent)
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