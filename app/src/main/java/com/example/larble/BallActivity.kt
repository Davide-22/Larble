package com.example.larble

import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BallActivity : AppCompatActivity(), SensorEventListener2 {
    var xPos: Float = 0.0f
    var xAccel: Float = 0.0f
    var xVel: Float = 0.0f

    var yPos: Float = 0.0f
    var yAccel: Float = 0.0f
    var yVel: Float = 0.0f

    var xMax: Float = 0F
    var yMax: Float = 0F
    lateinit var ballView : BallView

    var ballHeight = 0
    var ballWidth = 0



    lateinit var sensorManager: SensorManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ballView = BallView(this)
        setContentView(ballView)

        ballHeight = ballView.height
        ballWidth = ballView.width

        xMax = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        yMax = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        xPos = xMax/2
        yPos = yMax/2


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }
    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }
    override fun onStop() {
        sensorManager.unregisterListener(this)
        super.onStop()
    }


    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            xAccel = sensorEvent.values[0];
            yAccel = -sensorEvent.values[1];
            updateBall(ballView, xAccel, yAccel);
        }

    }

    private fun updateBall(ball : BallView, xAccel: Float, yAccel: Float) {
        var frameTime = 0.5f
        println("x $xAccel")
        println("y $yAccel")
        xVel += xAccel * frameTime
        yVel += yAccel * frameTime

        if(yAccel < 0) {
            frameTime = frameTime * 0.8f
        }



        var xS = xVel / 2 * frameTime
        var yS = yVel / 2 * frameTime

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
        ball.setParam(xPos, yPos)
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("ciao")
    }

    override fun onFlushCompleted(p0: Sensor?) {
        println("ciao")
    }
}