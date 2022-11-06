package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            val password: EditText = findViewById(R.id.editTextTextPassword)
            if (Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() && password.text.isNotEmpty()){
                intent = Intent(this, MenuActivity::class.java)
                val requestModel = LoginRequestModel(email.text.toString(),password.text.toString())

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.requestLogin(requestModel).enqueue(
                    object: Callback<LoginResponseClass> {
                        override fun onResponse(
                            call: Call<LoginResponseClass>,
                            response: Response<LoginResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                intent.putExtra("username", response.body()!!.username)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@MainActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        override fun onFailure(call: Call<LoginResponseClass>, t: Throwable) {
                            Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )

            }else{
                Toast.makeText(applicationContext, "Insert a valid email and password", Toast.LENGTH_LONG).show()
            }
        }

        signUp?.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


    }
}