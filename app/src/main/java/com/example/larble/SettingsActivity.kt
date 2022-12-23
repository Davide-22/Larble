package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.larble.requestModel.TokenRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.pow
import kotlin.math.sqrt


class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var myEdit: SharedPreferences.Editor
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val myLayout = findViewById<ConstraintLayout>(R.id.main)
        val checkRed: ImageView = findViewById(R.id.check_red)
        val checkYellow: ImageView = findViewById(R.id.check_yellow)
        val checkGreen: ImageView = findViewById(R.id.check_green)
        val checkBlue: ImageView = findViewById(R.id.check_blue)
        val red: View = findViewById(R.id.red)
        val yellow: View = findViewById(R.id.yellow)
        val green: View = findViewById(R.id.green)
        val blue: View = findViewById(R.id.blue)

        val ballView = BallView(this)
        val ballSrc: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ball2)
        val ball : Bitmap = Bitmap.createScaledBitmap(ballSrc, 200, 200, true)
        ballView.bitmaps[0] = ball
        val display = windowManager.defaultDisplay
        val width = display.width.toFloat()
        val height = display.height.toFloat()
        val diagonal = sqrt(width.pow(2)+height.pow(2))
        val x = 0.19220635f
        val y = 0.26073593f
        ballView.setParam(x*diagonal,y*diagonal)

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sharedPreferences.getString("token", "").toString()
        myEdit = sharedPreferences.edit()

        val colorBall = sharedPreferences.getString("colorBall", "")

        when (colorBall) {
            "#ea0b0b" -> {
                checkRed.visibility=View.VISIBLE
            }
            "#59FF00" -> {
                checkGreen.visibility=View.VISIBLE
            }
            "#0040FF" -> {
                checkBlue.visibility=View.VISIBLE
            }
            else -> {
                checkYellow.visibility=View.VISIBLE
            }
        }
        if(colorBall!= ""){
            ballView.firstPaint.colorFilter = PorterDuffColorFilter(Color.parseColor(colorBall), PorterDuff.Mode.SRC_IN)
        }
        myLayout.addView(ballView)

        red.setOnClickListener{
            checkRed.visibility=View.VISIBLE
            checkYellow.visibility=View.INVISIBLE
            checkGreen.visibility=View.INVISIBLE
            checkBlue.visibility=View.INVISIBLE
            val filter: ColorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN)
            ballView.firstPaint.colorFilter=filter
            myEdit.putString("colorBall", "#ea0b0b")
        }
        yellow.setOnClickListener{
            checkRed.visibility=View.INVISIBLE
            checkYellow.visibility=View.VISIBLE
            checkGreen.visibility=View.INVISIBLE
            checkBlue.visibility=View.INVISIBLE
            ballView.firstPaint.colorFilter=null
            myEdit.putString("colorBall", "")
        }
        green.setOnClickListener{
            checkRed.visibility=View.INVISIBLE
            checkYellow.visibility=View.INVISIBLE
            checkGreen.visibility=View.VISIBLE
            checkBlue.visibility=View.INVISIBLE
            val filter: ColorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_IN)
            ballView.firstPaint.colorFilter=filter
            myEdit.putString("colorBall", "#59FF00")
        }
        blue.setOnClickListener{
            checkRed.visibility=View.INVISIBLE
            checkYellow.visibility=View.INVISIBLE
            checkGreen.visibility=View.INVISIBLE
            checkBlue.visibility=View.VISIBLE
            val filter: ColorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.blue), PorterDuff.Mode.SRC_IN)
            ballView.firstPaint.colorFilter=filter
            myEdit.putString("colorBall", "#0040FF")
            myEdit.apply()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        myEdit.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                val requestModel = TokenRequestModel(token)

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.playerInfo(requestModel).enqueue(
                    object: Callback<PlayerResponseClass> {
                        override fun onResponse(
                            call: Call<PlayerResponseClass>,
                            response: Response<PlayerResponseClass>
                        ){
                            if(response.body()!!.status == "false"){
                                Toast.makeText(this@SettingsActivity, response.body()!!.msg, Toast.LENGTH_LONG).show()
                            }else{
                                intent = Intent(this@SettingsActivity, AccountActivity::class.java)
                                intent.putExtra("account", response.body())
                                startActivity(intent)
                            }
                        }
                        override fun onFailure(call: Call<PlayerResponseClass>, t: Throwable) {
                            intent = Intent(this@SettingsActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                )

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}