package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val difficulty : String= sharedPreferences.getString("difficulty", "").toString()
        val myEdit = sharedPreferences.edit()

        val result: String = intent.getStringExtra("result").toString()
        val type: String = intent.getStringExtra("type").toString()
        val buttons: String = intent.getStringExtra("difficulty").toString()
        val textResult: TextView = findViewById(R.id.result)
        val score: TextView = findViewById(R.id.score)
        val button : Button = findViewById(R.id.Continue)
        "You $result".also { textResult.text = it }
        when (result) {
            "win" -> {
                if(type == "multiplayer") "Points:        +10".also { score.text = it }
                else {
                    if(buttons == "easy" && difficulty=="100"){
                        myEdit.putString("difficulty", "110")
                        "You can try the next level".also {score.text = it }
                    }else if (buttons == "medium" && difficulty=="110"){
                        myEdit.putString("difficulty", "111")
                        "You can try the next level".also {score.text = it }
                    }else if (buttons == "hard" && difficulty=="111"){
                        "You can try the multiplayer".also{score.text = it}
                    }
                    myEdit.apply()
                }
            }
            "lose" -> {
                if(type == "multiplayer") "Points:         -5".also { score.text = it }
                else "You have to train more".also {score.text = it }
            }
        }

        button.setOnClickListener {
            if(type == "multiplayer"){
                intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }else{
                intent = Intent(this, SinglePlayerActivity::class.java)
                intent.putExtra("buttons", difficulty)
                startActivity(intent)
            }
        }


        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }
}