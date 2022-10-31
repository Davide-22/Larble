package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private var login: Button? = null
    private var signUp: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login = findViewById(R.id.login)
        signUp = findViewById(R.id.sign_up)

        login?.setOnClickListener {
            val email: EditText = findViewById(R.id.editTextTextEmailAddress)
            intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("email",email.text.toString())
            startActivity(intent)
        }

        signUp?.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


    }
}