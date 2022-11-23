package com.example.larble

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)

        val changeUsername: ImageButton = findViewById(R.id.edit_username)
        val changePassword: Button = findViewById(R.id.edit_password)
        val rootLayout = findViewById<ConstraintLayout>(R.id.accountLayout)

        val logout: Button = findViewById(R.id.log_out)

        var text : TextView = findViewById(R.id.username)
        val username: String? = sh.getString("username", "")
        "Username: $username".also { text.text = it }
        text = findViewById(R.id.email)
        val email: String? = intent.getStringExtra("email")
        "Email: $email".also { text.text = it }
        text = findViewById(R.id.wins)
        val wins: String? = intent.getStringExtra("wins")
        "Wins: $wins".also { text.text = it }
        text = findViewById(R.id.total_games)
        val totalGames: String? = intent.getStringExtra("total_games")
        "TotalGames: $totalGames".also { text.text = it }
        text = findViewById(R.id.score)
        val score: String? = intent.getStringExtra("score")
        "Score: $score".also { text.text = it }

        val profilePicture: String? = intent.getStringExtra("profile_picture")
        val picture: ImageView = findViewById(R.id.picture)
        if (profilePicture == null) {
            picture.visibility = View.VISIBLE
        }else{
            picture.visibility = View.INVISIBLE
        }

        logout.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("token", "")
            myEdit.putString("username", "")
            myEdit.apply()
            startActivity(intent)
        }

        changeUsername.setOnClickListener{
            val inflater: LayoutInflater = getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.popup_window,null)

            changePopUp(view,1)

            val newUsername: EditText = view.findViewById(R.id.new_username)
            newUsername.setText(username)
            val change: Button = view.findViewById(R.id.change_username)
            val checkUsername: ImageView = view.findViewById(R.id.check1)

            val popupWindow = PopupWindow(
                view,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true

            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.END
            popupWindow.exitTransition = slideOut

            change.setOnClickListener {
                if(checkUsername.visibility == View.VISIBLE){
                    val token: String? = sh.getString("token", "")
                    val requestModel = token?.let { it1 -> UsernameRequestModel(newUsername.text.toString(),it1) }

                    val response = ServiceBuilder.buildService(APIInterface::class.java)
                    if (requestModel != null) {
                        response.changeUsername(requestModel).enqueue(
                            object: Callback<ResponseClass> {
                                override fun onResponse(
                                    call: Call<ResponseClass>,
                                    response: Response<ResponseClass>
                                ){
                                    if(response.body()!!.status == "false"){
                                        Toast.makeText(this@AccountActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                            .show()
                                    }else{
                                        text = findViewById(R.id.username)
                                        "Username: ${newUsername.text}".also { text.text = it }
                                        val myEdit = sh.edit()
                                        myEdit.putString("username", newUsername.text.toString())
                                        myEdit.apply()
                                        popupWindow.dismiss()
                                        Toast.makeText(this@AccountActivity, "Username successfully changed", Toast.LENGTH_LONG).show()
                                    }
                                }
                                override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                    Toast.makeText(this@AccountActivity, t.toString(), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        )
                    }
                }else{
                    Toast.makeText(this@AccountActivity, "Username same as before", Toast.LENGTH_LONG)
                        .show()
                }
            }

            TransitionManager.beginDelayedTransition(rootLayout)
            popupWindow.showAtLocation(
                rootLayout, // Location to display popup window
                Gravity.CENTER, // Layout position to display popup
                0, // X offset
                0 // Y offset
            )

            newUsername.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if(s.toString() == username) checkUsername.visibility = View.INVISIBLE
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
        }

        changePassword.setOnClickListener {
            val inflater: LayoutInflater = getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.popup_window,null)

            changePopUp(view,2)

            val oldPassword: EditText= view.findViewById(R.id.old_password)
            val newPassword: EditText= view.findViewById(R.id.new_password)
            val confirmPassword: EditText= view.findViewById(R.id.confirm_password)
            val change: Button = view.findViewById(R.id.change_password)
            val checkPassword: ImageView = view.findViewById(R.id.check2)


            val popupWindow = PopupWindow(
                view,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true


            popupWindow.elevation = 10.0F

            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.END
            popupWindow.exitTransition = slideOut

            change.setOnClickListener {
                if(newPassword.text.isNotEmpty() && checkPassword.visibility == View.VISIBLE){
                    val token: String? = sh.getString("token", "")
                    val requestModel = token?.let { it1 -> PasswordRequestModel(it1, oldPassword.text.toString(), newPassword.text.toString()) }

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
                                    }else{
                                        popupWindow.dismiss()
                                        Toast.makeText(this@AccountActivity, "Password successfully changed", Toast.LENGTH_LONG).show()
                                    }
                                }
                                override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                    Toast.makeText(this@AccountActivity, t.toString(), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        )
                    }
                }else{
                    Toast.makeText(this@AccountActivity, "Invalid password entered", Toast.LENGTH_LONG).show()
                }
            }

            TransitionManager.beginDelayedTransition(rootLayout)
            popupWindow.showAtLocation(
                rootLayout, // Location to display popup window
                Gravity.CENTER, // Layout position to display popup
                0, // X offset
                0 // Y offset
            )

            confirmPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if(s.toString() == newPassword.text.toString()) checkPassword.visibility = View.VISIBLE
                    else checkPassword.visibility = View.INVISIBLE
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

    private fun changePopUp(view: View, type: Int){

        val newUsername: EditText = view.findViewById(R.id.new_username)
        val changeUsername: Button = view.findViewById(R.id.change_username)

        val oldPassword: EditText= view.findViewById(R.id.old_password)
        val newPassword: EditText= view.findViewById(R.id.new_password)
        val confirmPassword: EditText= view.findViewById(R.id.confirm_password)
        val changePassword: Button = view.findViewById(R.id.change_password)

        if(type == 1){
            newUsername.visibility = View.VISIBLE
            changeUsername.visibility = View.VISIBLE
            oldPassword.visibility = View.INVISIBLE
            newPassword.visibility = View.INVISIBLE
            confirmPassword.visibility = View.INVISIBLE
            changePassword.visibility = View.INVISIBLE
        }else{
            newUsername.visibility = View.INVISIBLE
            changeUsername.visibility = View.INVISIBLE
            oldPassword.visibility = View.VISIBLE
            newPassword.visibility = View.VISIBLE
            confirmPassword.visibility = View.VISIBLE
            changePassword.visibility = View.VISIBLE
        }

    }
}