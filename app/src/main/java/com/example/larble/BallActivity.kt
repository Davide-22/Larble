package com.example.larble

import android.content.Intent
import android.content.res.Resources
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


    private var ballHeight = 0
    private var ballWidth = 0

    private lateinit var sensorManager: SensorManager
    private lateinit var counter: CountDownTimer

    private var topWall: Float = -1f
    private var bottomWall: Float = -1f
    private var leftWall: Float = -1f
    private var rightWall: Float = -1f


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val colorBall = sharedPreferences.getString("colorBall","")
        setContentView(R.layout.activity_ball)
        val myLayout = findViewById<ConstraintLayout>(R.id.main)
        val counterText: TextView = findViewById(R.id.counter)
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
                val minutes = millisUntilFinished/60000
                val seconds = millisUntilFinished/(1000*(minutes+1))
                "$minutes:$seconds".also { counterText.text = it }
            }

            override fun onFinish() {
                intent = Intent(this@BallActivity, GameOverActivity::class.java)
                intent.putExtra("result", "lose")
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

        ballHeight = ballView.height
        ballWidth = ballView.width

        xMax = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        yMax = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        xPos = xMax
        yPos = yMax


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        GlobalScope.launch{
            var cond = true
            while(cond){
                val cell: Cell = mazeView.findCell(xPos,yPos)
                val walls: Array<Float> = mazeView.setLimits(cell)
                topWall = walls[0]
                leftWall = walls[1]
                bottomWall = walls[2]
                rightWall = walls[3]
                if(xPos==0f && yPos==0f) cond = false
            }
            counter.cancel()
            intent = Intent(this@BallActivity, GameOverActivity::class.java)
            intent.putExtra("result", "win")
            intent.putExtra("type", "single player")
            intent.putExtra("difficulty", difficulty)
            startActivity(intent)
        }
    }
    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
        counter.start()
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
            frameTime *= 0.8f
        }

        val xS = xVel / 2 * frameTime
        val yS = yVel / 2 * frameTime

        xPos -= xS
        yPos -= yS


        if (xPos > xMax - ballWidth - 100f) {
            xPos = xMax - ballWidth - 100f
            xVel = 0f

        } else if (xPos < ballWidth) {
            xPos = ballWidth.toFloat()
            xVel = 0f
        }
        if (yPos > yMax - ballHeight - 180) {
            yPos = yMax - ballHeight - 180
            yVel = 0f
        } else if (yPos < ballHeight) {
            yPos = ballHeight.toFloat()
            yVel = 0f
        }

        if (xPos > rightWall - ballWidth - 100f && rightWall!=-1f) {
            xPos = rightWall - ballWidth - 100f
            xVel = 0f

        } else if (xPos < leftWall && leftWall!=-1f) {
            xPos = leftWall+ballWidth.toFloat()
            xVel = 0f
        }
        if (yPos < topWall + ballHeight - 180 && topWall!=-1f) {
            yPos = topWall + ballHeight - 180
            yVel = 0f
        } else if (yPos > bottomWall && bottomWall!=-1f) {
            yPos = bottomWall-ballHeight.toFloat()
            yVel = 0f
        }
        ball.setParam(xPos, yPos)
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("ciao")
    }

    override fun onFlushCompleted(p0: Sensor?) {
        println("ciao")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }
}