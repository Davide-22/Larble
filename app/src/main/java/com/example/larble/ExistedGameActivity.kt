package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ExistedGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_existedgame)

        val game: Button = findViewById(R.id.play)


        game.setOnClickListener {
            intent = Intent(this, MultiPlayerGameActivity::class.java)
            startActivity(intent)
        }

    }
}