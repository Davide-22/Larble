package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val singlePlayer: Button = findViewById(R.id.single_player)
        val multiPlayer: Button = findViewById(R.id.multiplayer)
        val settings: ImageButton = findViewById(R.id.settings)
        val scoreboard: ImageButton = findViewById(R.id.scoreboard)

        val text: TextView = findViewById(R.id.name)
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val username: String? = sh.getString("username", "")
        "Ciao $username".also { text.text = it }

        singlePlayer.setOnClickListener {
            intent = Intent(this, SinglePlayerActivity::class.java)
            startActivity(intent)
        }

        multiPlayer.setOnClickListener {
            intent = Intent(this, MultiPlayerActivity::class.java)
            startActivity(intent)
        }

        settings.setOnClickListener{
            intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        scoreboard.setOnClickListener{
            intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                intent = Intent(this@MenuActivity, AccountActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAffinity()
        finish()
    }
}