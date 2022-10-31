package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MenuActivity : AppCompatActivity() {
    private var singlePlayer: Button? = null
    private var multiPlayer: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val text: TextView = findViewById(R.id.name)
        val passedData:String = intent.getStringExtra("email").toString()
        val name = "Ciao $passedData"
        text.text = name

        singlePlayer = findViewById(R.id.singleplayer)
        multiPlayer = findViewById(R.id.multiplayer)

        singlePlayer?.setOnClickListener {
            intent = Intent(this, SinglePlayerActivity::class.java)
            startActivity(intent)
        }

        multiPlayer?.setOnClickListener {
            intent = Intent(this, MultiPlayerActivity::class.java)
            startActivity(intent)
        }

    }
}