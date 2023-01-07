package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.larble.requestModel.GoogleRequestModel
import com.example.larble.requestModel.LoginRequestModel
import com.example.larble.responseModel.LoginResponseClass
import com.example.larble.responseModel.ResponseClass
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private lateinit var login: Button
    private lateinit var signUp: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var sharedPreferences : SharedPreferences

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleBtn: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.login)
        signUp = findViewById(R.id.sign_up)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        googleBtn = findViewById(R.id.google_btn)
        for (i in 0 until googleBtn.childCount) {
            val v: View = googleBtn.getChildAt(i)
            if (v is TextView) {
                "Sign in".also { v.text = it }
            }
        }
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)

        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()


        val google = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result.resultCode == RESULT_OK){
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if(task.getResult(ApiException::class.java)!=null){
                    handleResult(task)
                }else{
                    Toast.makeText(this@LoginActivity, "error", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        googleBtn.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            google.launch(signInIntent)
        }


        login.setOnClickListener {
            if (Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() && password.text.isNotEmpty()){
                intent = Intent(this, MenuActivity::class.java)
                val requestModel = LoginRequestModel(email.text.toString(), password.text.toString())

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.requestLogin(requestModel).enqueue(
                    object: Callback<LoginResponseClass> {
                        override fun onResponse(
                            call: Call<LoginResponseClass>,
                            response: Response<LoginResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                intent.putExtra("username", response.body()!!.username)
                                val myEdit = sharedPreferences.edit()
                                myEdit.putString("token", response.body()!!.msg)
                                myEdit.putString("username", response.body()!!.username)
                                myEdit.putString("google", false.toString())
                                myEdit.apply()
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@LoginActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponseClass>, t: Throwable) {
                            intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                )

            }else{
                Toast.makeText(applicationContext, "Insert valid email and password", Toast.LENGTH_LONG).show()
            }
        }

        signUp.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
                finish()
            }
        })


    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var intent = Intent(this@LoginActivity, MenuActivity::class.java)
                    val myEdit = sharedPreferences.edit()
                    myEdit.putString("username", account.displayName)
                    myEdit.putString("google", true.toString())
                    val requestModel = GoogleRequestModel(account.email.toString(),account.displayName.toString(),account.photoUrl.toString())

                    val response = ServiceBuilder.buildService(APIInterface::class.java)
                    response.loginGoogle(requestModel).enqueue(
                        object: Callback<ResponseClass> {
                            override fun onResponse(
                                call: Call<ResponseClass>,
                                response: Response<ResponseClass>
                            ){
                                if(response.body()!!.status=="true"){
                                    intent.putExtra("username", account.displayName)
                                    myEdit.putString("token", response.body()!!.msg)
                                    myEdit.apply()
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(this@LoginActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }

                            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    )
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

}