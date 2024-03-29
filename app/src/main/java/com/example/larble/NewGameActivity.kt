package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
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
    private lateinit var text: TextView
    private lateinit var sh: SharedPreferences
    private lateinit var token: String
    private lateinit var number: String
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)

        var cells  = intent.getSerializableExtra("labyrinth")
        if(cells != null){
            cells = cells as Array<Array<Cell>>
        }
        number = intent.getStringExtra("number").toString()
        text = findViewById(R.id.number)
        text.text = number
        code = text.text.toString().toInt()
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token= sh.getString("token", "").toString()
        val requestModel = GameCodeModel(number)

        intent = Intent(this, MultiPlayerGameActivity::class.java)
        val service = ServiceBuilder.buildService(APIInterface::class.java)

        job = GlobalScope.launch {
            while(!result) {
                try {
                    val callSync = requestModel.let { it1 -> service.checkForPlayer(it1) }
                    val response = callSync.execute()
                    if (response.body()!!.status == "true") {
                        intent.putExtra("number", code.toString())
                        intent.putExtra("labyrinth",cells)
                        startActivity(intent)
                        result = true
                    }
                } catch (ex: Exception) {
                    Log.d("error", Log.getStackTraceString(ex))
                }
                delay(1000)
            }
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                result = true
                job.cancel()
                val requestModel1 = GameCodeRequestModel(code,token)
                intent = Intent(this@NewGameActivity, MainActivity::class.java)

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.deleteGame(requestModel1).enqueue(
                    object: Callback<ResponseClass> {
                        override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                            startActivity(intent)
                        }

                        override fun onResponse(
                            call: Call<ResponseClass>,
                            response: Response<ResponseClass>
                        ) {
                        }
                    }
                )
                startActivity(intent)
            }
        })

    }
}