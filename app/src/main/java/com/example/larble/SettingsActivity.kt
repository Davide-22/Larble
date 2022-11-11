package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private var logout: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        logout = findViewById(R.id.log_out)

        logout?.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("token", "")
            myEdit.putString("username", "")
            myEdit.apply()
            startActivity(intent)
        }

    }
}