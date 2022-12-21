package com.example.larble

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.ResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var rootLayout : ConstraintLayout
    private lateinit var popupWindow : PopupWindow
    private lateinit var network : TextView
    private lateinit var server : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootLayout = findViewById(R.id.mainLayout)

        val inflater: LayoutInflater = getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.network_popup,null)
        network = view.findViewById(R.id.network)
        server = view.findViewById(R.id.server)
        val retry: Button = view.findViewById(R.id.retry)

        val display = windowManager.defaultDisplay
        val width = display.width
        val height = display.height

        popupWindow = PopupWindow(
            view,
            (width/1.1).toInt(),
            (height/3)
        )
        popupWindow.isFocusable = true
        popupWindow.elevation = 10.0F

        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        popupWindow.enterTransition = slideIn

        val slideOut = Slide()
        slideOut.slideEdge = Gravity.END
        popupWindow.exitTransition = slideOut

        retry.setOnClickListener {
            popupWindow.dismiss()
            verify()
        }
    }

    override fun onResume() {
        super.onResume()
        verify()
    }

    private fun verify(){
        intent = Intent(this@MainActivity, LoginActivity::class.java)
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token: String? = sh.getString("token", "")
        val requestModel = token?.let { TokenRequestModel(it) }

        val response = ServiceBuilder.buildService(APIInterface::class.java)
        if (requestModel != null) {
            response.verify(requestModel).enqueue(
                object: Callback<ResponseClass> {
                    override fun onResponse(
                        call: Call<ResponseClass>,
                        response: Response<ResponseClass>
                    ){
                        if(response.body()==null){
                            server.visibility=View.VISIBLE
                            TransitionManager.beginDelayedTransition(rootLayout)
                            popupWindow.showAtLocation(
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
                        popupWindow.showAtLocation(
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
}