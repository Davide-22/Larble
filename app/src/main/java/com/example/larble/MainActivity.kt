package com.example.larble

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.ResponseClass
import com.google.android.gms.auth.api.signin.GoogleSignIn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var rootLayout : ConstraintLayout
    private lateinit var popup : PopupWindow
    private lateinit var network : TextView
    private lateinit var server : TextView
    private lateinit var retry: Button
    private lateinit var sh: SharedPreferences
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootLayout = findViewById(R.id.mainLayout)

        val inflater: LayoutInflater = getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val nullParent: ViewGroup? = null
        val view = inflater.inflate(R.layout.network_popup,nullParent)
        network = view.findViewById(R.id.network)
        server = view.findViewById(R.id.server)
        retry = view.findViewById(R.id.retry)

        val metrics: DisplayMetrics = this.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        popup = PopupWindow(
            view,
            (width/1.1).toInt(),
            (height/3)
        )
        popup.isFocusable = true
        popup.elevation = 10.0F

        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        popup.enterTransition = slideIn

        val slideOut = Slide()
        slideOut.slideEdge = Gravity.END
        popup.exitTransition = slideOut

        retry.setOnClickListener {
            popup.dismiss()
            verify()
        }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }else{
            verify()
        }
    }

    private fun verify(){
        intent = Intent(this@MainActivity, LoginActivity::class.java)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh.getString("token", "").toString()
        val requestModel = TokenRequestModel(token)

        val response = ServiceBuilder.buildService(APIInterface::class.java)
        response.verify(requestModel).enqueue(
            object: Callback<ResponseClass> {
                override fun onResponse(
                    call: Call<ResponseClass>,
                    response: Response<ResponseClass>
                ){
                    if(response.body()==null){
                        server.visibility=View.VISIBLE
                        TransitionManager.beginDelayedTransition(rootLayout)
                        popup.showAtLocation(
                            rootLayout, // Location to display popup window
                            Gravity.CENTER, // Layout position to display popup
                            0, // X offset
                            0 // Y offset
                        )
                    }else{
                        if(response.body()!!.status=="true"){
                            intent = Intent(this@MainActivity, MenuActivity::class.java)
                        }
                        startActivity(intent)
                    }
                }
                override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                    network.visibility=View.VISIBLE
                    TransitionManager.beginDelayedTransition(rootLayout)
                    popup.showAtLocation(
                        rootLayout, // Location to display popup window
                        Gravity.CENTER, // Layout position to display popup
                        0, // X offset
                        0 // Y offset
                    )
                }
            }
        )
    }
}