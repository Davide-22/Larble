package com.example.larble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.random.Random

class NewGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)

        val start = 0
        val end = 999

        val rand = (start..end).random(Random(System.nanoTime()))
        val text: TextView = findViewById(R.id.number)
        var number = "$rand"
        if(rand in 0..9) number = "00$number"
        else if(rand in 10..99) number = "0$number"
        text.text = number

    }
}