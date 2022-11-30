package com.example.larble

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.View

class BallView(context: Context) : View(context) {
    //var ball: Bitmap
    var xPos : Float = 0.0f
    var yPos : Float = 0.0f
    val ballSrc = BitmapFactory.decodeResource(resources, R.drawable.ball)
    var ball : Bitmap = Bitmap.createScaledBitmap(ballSrc, 100, 100, true)


    fun setParam(x: Float, y: Float) {
        xPos = x
        yPos = y
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(ball, xPos, yPos, null)
        invalidate()

    }
}
