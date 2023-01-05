package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.larble.requestModel.LoginRequestModel
import com.example.larble.responseModel.LoginResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {
    private var login: Button? = null
    private var signUp: Button? = null
    private var email: EditText? = null
    private var password: EditText? = null

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    private var google_btn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.login)
        signUp = findViewById(R.id.sign_up)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        google_btn = findViewById(R.id.google_btn)

        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        google_btn?.setOnClickListener { view: View? ->
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            signInGoogle()
        }


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

        signUp?.setOnClickListener {
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

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //SavedPreference.setEmail(this, account.email.toString())
                //SavedPreference.setUsername(this, account.displayName.toString())
                /*val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()*/
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            /*startActivity(
                Intent(
                    this, MenuActivity
                    ::class.java
                )
            )
            finish()*/
        }
    }

}