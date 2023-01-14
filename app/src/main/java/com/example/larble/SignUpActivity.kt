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

    private lateinit var signUp: Button
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var checkUsername: ImageView
    private lateinit var checkEmail: ImageView
    private lateinit var checkConfirmPassword: ImageView
    private lateinit var checkPassword: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        signUp= findViewById(R.id.sign_up1)
        username = findViewById(R.id.editTextTextPersonName2)
        email = findViewById(R.id.editTextTextEmailAddress2)
        password = findViewById(R.id.editTextTextPassword2)
        confirmPassword = findViewById(R.id.editTextTextPassword3)
        checkUsername = findViewById(R.id.checkUsername)
        checkEmail = findViewById(R.id.checkEmail)
        checkConfirmPassword = findViewById(R.id.checkConfirmPassword)
        checkPassword = findViewById(R.id.checkPassword)

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
                            if(response.body()== null){
                                Toast.makeText(this@SignUpActivity, "Connection with the server failed", Toast.LENGTH_LONG)
                                    .show()
                                intent = Intent(this@SignUpActivity, MenuActivity::class.java)
                                startActivity(intent)
                            }
                            else if(response.body()!!.status=="true"){
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