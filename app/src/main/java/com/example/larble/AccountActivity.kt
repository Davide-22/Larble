package com.example.larble

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val changePassword: ImageButton = findViewById(R.id.edit_password)
        val rootLayout = findViewById<ConstraintLayout>(R.id.accountLayout)

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
            val inflater: LayoutInflater = getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.popup_window,null)

            val oldPassword: EditText= view.findViewById(R.id.old_password)
            val newPassword: EditText= view.findViewById(R.id.new_password)
            val confirmPassword: EditText= view.findViewById(R.id.confirm_password)
            val change: Button = view.findViewById(R.id.change_password)


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
                if(newPassword.text.isNotEmpty() && newPassword.text.toString() == confirmPassword.text.toString()){
                    val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
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
                }
            }

            TransitionManager.beginDelayedTransition(rootLayout)
            popupWindow.showAtLocation(
                rootLayout, // Location to display popup window
                Gravity.CENTER, // Layout position to display popup
                0, // X offset
                0 // Y offset
            )
        }
    }
}