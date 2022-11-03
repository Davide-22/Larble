package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MultiPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        val newGame: Button = findViewById(R.id.new_game)
        val existedGame: Button = findViewById(R.id.existed_game)


        newGame.setOnClickListener {
            intent = Intent(this, NewGameActivity::class.java)
            startActivity(intent)
        }

        existedGame.setOnClickListener {
            intent = Intent(this, ExistedGameActivity::class.java)
            startActivity(intent)
        }
    }
}