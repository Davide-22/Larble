package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MultiPlayerActivity : AppCompatActivity() {
    private var newGame: Button? = null
    private var game: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        newGame = findViewById(R.id.newgame)
        game = findViewById(R.id.game)


        newGame?.setOnClickListener {
            intent = Intent(this, NewGameActivity::class.java)
            startActivity(intent)
        }

        game?.setOnClickListener {
            intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
}