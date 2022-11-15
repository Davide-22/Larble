package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NewGameActivity : AppCompatActivity() {
    private var result = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)

        val game: Button = findViewById(R.id.play2)
        val text: TextView = findViewById(R.id.number)
        text.text = intent.getStringExtra("number")
        val requestModel = intent.getStringExtra("number")?.let { GameCodeModel(it) }

        game.setOnClickListener {
            intent = Intent(this, MultiPlayerGameActivity::class.java)
            val service = ServiceBuilder.buildService(APIInterface::class.java)

            Thread {
                while(!result) {
                    try {
                        val callSync = requestModel?.let { it1 -> service.checkForPlayer(it1) }
                        val response = callSync?.execute()
                        if (response != null) {
                            if (response.body()!!.status == "true") {
                                result = true
                                startActivity(intent)
                            }
                        }

                    } catch (ex: Exception) {
                        Log.e("error", Log.getStackTraceString(ex))
                    }
                }
            }.start()
        }
    }

}