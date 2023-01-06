package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.larble.requestModel.GameCodeRequestModel
import com.example.larble.requestModel.PositionRequestModel
import com.example.larble.responseModel.PositionResponseClass
import com.example.larble.responseModel.ResponseClass
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class MultiPlayerGameActivity : AppCompatActivity(), SensorEventListener2 {
    private var xPos: Float = 0.0f
    private var xAccel: Float = 0.0f
    private var xVel: Float = 0.0f

    private var yPos: Float = 0.0f
    private var yAccel: Float = 0.0f
    private var yVel: Float = 0.0f

    private var xMax: Float = 0F
    private var yMax: Float = 0F
    private lateinit var ballView : BallView
    private lateinit var mazeView: MazeView

    private var ballHeight = 0
    private var ballWidth = 0

    private lateinit var sensorManager: SensorManager
    private var job: Job = Job()
    private var job1: Job = Job()
    private var job2: Job = Job()
    private var result = true
    private var win = false
    private lateinit var sh: SharedPreferences
    private lateinit var token: String
    private lateinit var number: String
    private var positions: Queue<Array<Float>> = LinkedList()
    private lateinit var lastPos: Array<Float>
    private lateinit var colorBall: String
    private lateinit var layout: ConstraintLayout


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh.getString("token", "").toString()
        colorBall = sh.getString("colorBall", "").toString()
        setContentView(R.layout.activity_ball)
        layout = findViewById(R.id.main)
        val cells = intent.getSerializableExtra("labyrinth") as Array<Array<Cell>>
        number = intent.getStringExtra("number").toString()

        ballView = BallView(this)
        mazeView = MazeView(this)
        mazeView.setCells(cells)
        if(colorBall!=""){
            ballView.firstPaint.colorFilter = PorterDuffColorFilter(Color.parseColor(colorBall), PorterDuff.Mode.SRC_IN)
        }
        layout.addView(ballView)
        layout.addView(mazeView)

        ballHeight = ballView.height
        ballWidth = ballView.width

        xMax = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        yMax = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        xPos = xMax
        yPos = yMax

        lastPos = arrayOf(xMax,yMax)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager


        val metrics: DisplayMetrics = this.resources.displayMetrics
        val width = metrics.widthPixels.toFloat()
        val height = metrics.heightPixels.toFloat()
        val diagonal = sqrt(width.pow(2)+height.pow(2))

        job = GlobalScope.launch {
            while(result){
                val requestModel = PositionRequestModel(token, number.toInt(), ballView.positions[0]/ diagonal, ballView.positions[1]/diagonal)
                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.takePosition(requestModel).enqueue(
                    object: Callback<PositionResponseClass> {
                        override fun onResponse(
                            call: Call<PositionResponseClass>,
                            response: Response<PositionResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                if(response.body()!!.win){
                                    if(!win){
                                        win = response.body()!!.win
                                        var x = response.body()!!.x*diagonal
                                        var y = response.body()!!.y*diagonal
                                        val pos: Array<Float> = arrayOf(x,y)
                                        for(i in 1..9){
                                            val t = (i.toFloat())/10
                                            x = lastPos[0] + t*(pos[0]-lastPos[0])
                                            y = lastPos[1] + t*(pos[1]-lastPos[1])
                                            val newPos: Array<Float> = arrayOf(x,y)

                                            positions.add(newPos)
                                        }
                                        positions.add(pos)
                                        lastPos = pos
                                    }
                                }else{
                                    if(ballView.bitmaps.size == 1){
                                        val ballSrc = BitmapFactory.decodeResource(resources, R.drawable.ball2)
                                        val ball : Bitmap = Bitmap.createScaledBitmap(ballSrc, 100, 100, true)
                                        ballView.bitmaps.add(ball)
                                    }
                                    var x = response.body()!!.x*diagonal
                                    var y = response.body()!!.y*diagonal
                                    val pos: Array<Float> = arrayOf(x,y)
                                    for(i in 1..9){
                                        val t = (i.toFloat())/10
                                        x = lastPos[0] + t*(pos[0]-lastPos[0])
                                        y = lastPos[1] + t*(pos[1]-lastPos[1])
                                        val newPos: Array<Float> = arrayOf(x,y)

                                        positions.add(newPos)
                                    }
                                    positions.add(pos)
                                    lastPos = pos
                                }
                            }else{
                                Toast.makeText(this@MultiPlayerGameActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<PositionResponseClass>, t: Throwable) {
                            Toast.makeText(this@MultiPlayerGameActivity, t.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )
                delay(90)
            }
        }

        job2 = GlobalScope.launch {
            while(!win || !positions.isEmpty()){
                val pos: Array<Float>? = positions.poll()
                if(pos!=null){
                    ballView.positions[2] = pos[0]
                    ballView.positions[3] = pos[1]
                }
                if(positions.size!=0) delay((90/positions.size).toLong())
            }
            result = false
            job.cancel()
            val requestModel = GameCodeRequestModel(number.toInt(),token)

            val response = ServiceBuilder.buildService(APIInterface::class.java)
            response.deleteFinishedGame(requestModel).enqueue(
                object: Callback<ResponseClass> {
                    override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                        Toast.makeText(this@MultiPlayerGameActivity, t.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onResponse(
                        call: Call<ResponseClass>,
                        response: Response<ResponseClass>
                    ) {
                    }
                }
            )
            intent.putExtra("result","lost")
            intent.putExtra("type", "multiplayer")
            startActivity(intent)
        }

        job1 = GlobalScope.launch {
            intent = Intent(this@MultiPlayerGameActivity, GameOverActivity::class.java)
            while(true){
                if(xPos in 0f..80.0F && yPos in 0f..80.0F) {
                    break
                }
            }
            if(!win){
                result = false
                job.cancel()
                job2.cancel()
                val requestModel = GameCodeRequestModel(number.toInt(), token)
                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.winning(requestModel).enqueue(
                    object: Callback<ResponseClass> {
                        override fun onResponse(
                            call: Call<ResponseClass>,
                            response: Response<ResponseClass>
                        ){
                            if(response.body()!!.status=="false"){
                                Toast.makeText(this@MultiPlayerGameActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                        }
                    }
                )
                intent.putExtra("result","win")
                intent.putExtra("type", "multiplayer")
            }
            startActivity(intent)
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })


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
        ball.setParam(xPos, yPos)
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("ciao")
    }

    override fun onFlushCompleted(p0: Sensor?) {
        println("ciao")
    }
}