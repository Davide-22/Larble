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
import com.example.larble.requestModel.SignUpRequestModel
import com.example.larble.responseModel.ResponseClass
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
        val checkUsername: ImageView = findViewById(R.id.checkUsername)
        val checkEmail: ImageView = findViewById(R.id.checkEmail)
        val checkConfirmPassword: ImageView = findViewById(R.id.checkConfirmPassword)
        val checkPassword: ImageView = findViewById(R.id.checkPassword)

        signUp.setOnClickListener {
            if(checkUsername.visibility==View.VISIBLE && checkEmail.visibility==View.VISIBLE && checkConfirmPassword.visibility==View.VISIBLE && checkPassword.visibility==View.VISIBLE){
                intent = Intent(this, LoginActivity::class.java)
                val requestModel = SignUpRequestModel(username.text.toString(),email.text.toString(),password.text.toString())

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.requestSignUp(requestModel).enqueue(
                    object: Callback<ResponseClass>{
                        override fun onResponse(
                            call: Call<ResponseClass>,
                            response: Response<ResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@SignUpActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                            intent = Intent(this@SignUpActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                )

            }else if(password.text.toString() != confirmPassword.text.toString()){
                Toast.makeText(applicationContext, "Passwords don't matches", Toast.LENGTH_LONG).show()
            } else{
                Toast.makeText(applicationContext, "Fill in all fields", Toast.LENGTH_LONG).show()

            }
        }

        username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.isEmpty()) checkUsername.visibility = View.INVISIBLE
                else checkUsername.visibility = View.VISIBLE
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
                if(Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) checkEmail.visibility = View.VISIBLE
                else checkEmail.visibility = View.INVISIBLE
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
                if(s.isEmpty()) checkPassword.visibility = View.INVISIBLE
                else checkPassword.visibility = View.VISIBLE
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
                if(s.toString() == password.text.toString()) checkConfirmPassword.visibility = View.VISIBLE
                else checkConfirmPassword.visibility = View.INVISIBLE
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