package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class NewGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)

        val game: Button = findViewById(R.id.play2)
        val text: TextView = findViewById(R.id.number)
        text.text = intent.getStringExtra("number")

        game.setOnClickListener {
            intent = Intent(this, MultiPlayerGameActivity::class.java)
            startActivity(intent)
        }

    }
}