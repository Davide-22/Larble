package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private var login: Button? = null
    private var signUp: Button? = null
    private var email: EditText? = null
    private var password: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login = findViewById(R.id.login)
        signUp = findViewById(R.id.sign_up)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)

        login?.setOnClickListener {
            if (Patterns.EMAIL_ADDRESS.matcher(email!!.text.toString()).matches() && password!!.text.isNotEmpty()){
                intent = Intent(this, MenuActivity::class.java)
                val requestModel = LoginRequestModel(email!!.text.toString(),password!!.text.toString())

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.requestLogin(requestModel).enqueue(
                    object: Callback<LoginResponseClass> {
                        override fun onResponse(
                            call: Call<LoginResponseClass>,
                            response: Response<LoginResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                intent.putExtra("username", response.body()!!.username)
                                val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                                val myEdit = sharedPreferences.edit()
                                myEdit.putString("token", response.body()!!.msg)
                                myEdit.putString("username", response.body()!!.username)
                                myEdit.apply()
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
                Toast.makeText(applicationContext, "Insert valid email and password", Toast.LENGTH_LONG).show()
            }
        }

        signUp?.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        verify()
    }

    private fun verify(){
        intent = Intent(this, MenuActivity::class.java)
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token: String? = sh.getString("token", "")
        val requestModel = token?.let { VerifyRequestModel(it) }

        val response = ServiceBuilder.buildService(APIInterface::class.java)
        if (requestModel != null) {
            response.verify(requestModel).enqueue(
                object: Callback<ResponseClass> {
                    override fun onResponse(
                        call: Call<ResponseClass>,
                        response: Response<ResponseClass>
                    ){
                        if(response.body()!!.status=="true"){
                            startActivity(intent)
                        }
                    }
                    override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            )
        }
    }

}