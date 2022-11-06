package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val signUp: Button = findViewById(R.id.sign_up1)
        val username: EditText = findViewById(R.id.editTextTextPersonName2)
        val email: EditText = findViewById(R.id.editTextTextEmailAddress2)
        val password: EditText = findViewById(R.id.editTextTextPassword2)
        val confirmPassword: EditText = findViewById(R.id.editTextTextPassword3)
        val check1: ImageView = findViewById(R.id.check1)
        val check2: ImageView = findViewById(R.id.check2)
        val check3: ImageView = findViewById(R.id.check3)
        val check4: ImageView = findViewById(R.id.check4)

        signUp.setOnClickListener {
            if(check1.visibility==View.VISIBLE && check2.visibility==View.VISIBLE && check3.visibility==View.VISIBLE && check4.visibility==View.VISIBLE){
                intent = Intent(this, MainActivity::class.java)
                val requestModel = SignUpRequestModel(username.text.toString(),email.text.toString(),password.text.toString())

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.requestSignUp(requestModel).enqueue(
                    object: Callback<SignUpResponseClass>{
                        override fun onResponse(
                            call: Call<SignUpResponseClass>,
                            response: Response<SignUpResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@SignUpActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        override fun onFailure(call: Call<SignUpResponseClass>, t: Throwable) {
                            Toast.makeText(this@SignUpActivity, t.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )

            }else if(password.text.toString() != confirmPassword.text.toString()){
                Toast.makeText(applicationContext, "Password different from Confirm password", Toast.LENGTH_LONG).show()
            } else{
                Toast.makeText(applicationContext, "Fill in all fields", Toast.LENGTH_LONG).show()

            }
        }
        username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.isEmpty()) check1.visibility = View.INVISIBLE
                else check1.visibility = View.VISIBLE
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) check2.visibility = View.VISIBLE
                else check2.visibility = View.INVISIBLE
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.isEmpty()) check4.visibility = View.INVISIBLE
                else check4.visibility = View.VISIBLE
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.toString() == password.text.toString()) check3.visibility = View.VISIBLE
                else check3.visibility = View.INVISIBLE
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
    }
}