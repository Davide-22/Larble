package com.example.larble

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Color.parseColor
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BallActivity : AppCompatActivity(), SensorEventListener2 {
    private var xPos: Float = 0.0f
    private var xAccel: Float = 0.0f
    private var xVel: Float = 0.0f

    private var yPos: Float = 0.0f
    private var yAccel: Float = 0.0f
    private var yVel: Float = 0.0f

    private var xMax: Float = 0F
    private var yMax: Float = 0F
    private lateinit var ballView : BallView
    private lateinit var mazeView : MazeView


    private var ballHeight = 100f
    private var ballWidth = 100f

    private lateinit var sensorManager: SensorManager
    private lateinit var counter: CountDownTimer

    private var topWall: Float = -1f
    private var bottomWall: Float = -1f
    private var leftWall: Float = -1f
    private var rightWall: Float = -1f

    private var minutes: Long = 0L
    private var seconds: Long = 0L


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val colorBall = sharedPreferences.getString("colorBall","")
        setContentView(R.layout.activity_ball)
        val myLayout = findViewById<ConstraintLayout>(R.id.main)
        myLayout.setWillNotDraw(false)
        val counterText: TextView = findViewById(R.id.counter)
        if(isDarkTheme()){
            myLayout.setBackgroundColor(Color.BLACK)
            counterText.setTextColor(Color.WHITE)
        }
        val difficulty: String = intent.getStringExtra("difficulty").toString()
        val milliseconds: Long = when (difficulty) {
            "easy" -> {
                60000
            }
            "medium" -> {
                45000
            }
            else -> {
                30000
            }
        }
        counter = object : CountDownTimer(milliseconds,1000) {
            override fun onTick(millisUntilFinished: Long) {
                minutes = millisUntilFinished/60000
                seconds = millisUntilFinished/(1000*(minutes+1))
                if(minutes < 10){
                    if(minutes == 0L && seconds<11){
                        counterText.setTextColor(getColor(R.color.red))
                    }
                    if(seconds < 10){
                        "0$minutes:0$seconds".also { counterText.text = it }
                    }else{
                        "0$minutes:$seconds".also { counterText.text = it }
                    }
                }else{
                    if(seconds <= 10){
                        "$minutes:0$seconds".also { counterText.text = it }
                    }else{
                        "$minutes:$seconds".also { counterText.text = it }
                    }
                }
            }

            override fun onFinish() {
                intent = Intent(this@BallActivity, GameOverActivity::class.java)
                intent.putExtra("result", "lost :(")
                startActivity(intent)
            }

        }

        ballView = BallView(this)
        mazeView = MazeView(this)

        if(colorBall!=""){
            ballView.firstPaint.colorFilter = PorterDuffColorFilter(parseColor(colorBall), PorterDuff.Mode.SRC_IN)
        }

        myLayout.addView(ballView)
        myLayout.addView(mazeView)


        xMax = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        yMax = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        xPos = xMax
        yPos = yMax - ballHeight - 80f


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        GlobalScope.launch{
            var cond = true
            while(cond){

                val cell: Array<Cell> = mazeView.findCell(xPos,yPos)
                val walls: Array<Float> = mazeView.setLimits(cell, yVel, xVel)
                topWall = walls[0]
                leftWall = walls[1]
                bottomWall = walls[2]
                rightWall = walls[3]
                if(xPos in 0f..80.0F && yPos in 0f..110.0F) cond = false
            }
            counter.cancel()
            intent = Intent(this@BallActivity, GameOverActivity::class.java)
            intent.putExtra("result", "won!!!")
            intent.putExtra("type", "single player")
            intent.putExtra("difficulty", difficulty)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }


    private fun isDarkTheme(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
        if(minutes==0L && seconds==0L){
            counter.start()
        }
    }
    override fun onStop() {
        sensorManager.unregisterListener(this)
        super.onStop()
    }


    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            xAccel = sensorEvent.values[0]
            yAccel = -sensorEvent.values[1]
            updateBall(ballView, xAccel, yAccel)
        }

    }

    private fun updateBall(ball : BallView, xAccel: Float, yAccel: Float) {
        var frameTime = 0.5f
        xVel += xAccel * frameTime
        yVel += yAccel * frameTime

        if(yAccel < 0) {
            frameTime *= 0.5f
        }


        val xS = xVel / 2 * frameTime
        val yS = yVel / 2 * frameTime

        xPos -= xS
        yPos -= yS



        if (xPos > xMax ) {
            xPos = xMax
            xVel = 0f

        } else if (xPos < 0f) {
            xPos = 0f
            xVel = 0f
        }
        if (yPos > yMax - ballHeight - 80f) {
            yPos = yMax - ballHeight - 80f
            yVel = 0f
        } else if (yPos < 0f) {
            yPos = 0f
            yVel = 0f
        }

        if(xPos != xMax && yPos != yMax) {
            if (xPos > rightWall - ballWidth+1 && rightWall!=-1f) {
                xPos = rightWall - ballWidth+1
                xVel = 0f

            } else if (xPos <= leftWall && leftWall!=-1f) {
                xPos = leftWall
                xVel = 0f
            }
            if (yPos <= topWall && topWall!=-1f) {
                yPos = topWall
                yVel = 0f
            } else if (yPos > bottomWall - ballHeight+1 && bottomWall!=-1f) {
                yPos = bottomWall - ballHeight+1
                yVel = 0f
            }
        }
        ball.setParam(xPos, yPos)
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("onAccuracyChanged")
    }

    override fun onFlushCompleted(p0: Sensor?) {
        println("onFlushCompleted")
    }
}