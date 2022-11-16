package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val changePassword: Button = findViewById(R.id.button)

        val logout: Button = findViewById(R.id.log_out)

        logout.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("token", "")
            myEdit.putString("username", "")
            myEdit.apply()
            startActivity(intent)
        }

        changePassword.setOnClickListener {
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val token: String? = sh.getString("token", "")
            val requestModel = token?.let { it1 -> PasswordRequestModel(it1, "", "") }

            val response = ServiceBuilder.buildService(APIInterface::class.java)
            if (requestModel != null) {
                response.changePassword(requestModel).enqueue(
                    object: Callback<ResponseClass> {
                        override fun onResponse(
                            call: Call<ResponseClass>,
                            response: Response<ResponseClass>
                        ){
                            if(response.body()!!.status == "false"){
                                Toast.makeText(this@AccountActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                            Toast.makeText(this@AccountActivity, t.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )
            }
        }
    }
}