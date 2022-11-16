package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button

class SinglePlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleplayer)
        val easy: Button = findViewById(R.id.easy)
        val medium: Button = findViewById(R.id.medium)
        val hard: Button = findViewById(R.id.hard)

        easy.setOnClickListener {
            intent = Intent(this, SinglePlayerGameActivity::class.java)
            startActivity(intent)
        }
        medium.setOnClickListener {
            intent = Intent(this, SinglePlayerGameActivity::class.java)
            startActivity(intent)
        }
        hard.setOnClickListener {
            intent = Intent(this, SinglePlayerGameActivity::class.java)
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
                intent = Intent(this@SinglePlayerActivity, AccountActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}