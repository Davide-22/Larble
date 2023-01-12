package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    private lateinit var sh: SharedPreferences
    private lateinit var difficulty: String
    private lateinit var result: String
    private lateinit var type: String
    private lateinit var buttons: String
    private lateinit var textResult: TextView
    private lateinit var button: Button
    private lateinit var score: TextView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        difficulty = sh.getString("difficulty", "").toString()
        val myEdit = sh.edit()

        result = intent.getStringExtra("result").toString()
        type = intent.getStringExtra("type").toString()
        buttons = intent.getStringExtra("difficulty").toString()
        textResult = findViewById(R.id.result)
        score = findViewById(R.id.score)
        button = findViewById(R.id.Continue)
        "You $result".also { textResult.text = it }
        when (result) {
            "won!!!" -> {
                if(type == "multiplayer") "Score:        +10".also { score.text = it }
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
            "lost :(" -> {
                if(type == "multiplayer") "Score:         -5".also { score.text = it }
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