package com.example.larble

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.View

class BallView(context: Context) : View(context) {
    val ballSrc = BitmapFactory.decodeResource(resources, R.drawable.ball2)
    var ball : Bitmap = Bitmap.createScaledBitmap(ballSrc, 100, 100, true)
    var bitmaps: MutableList<Bitmap> = arrayListOf(ball)
    var positions= FloatArray(4)

    fun setParam(x: Float, y: Float) {
        positions[0] = x
        positions[1] = y
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(i in 0 until bitmaps.size){
            canvas.drawBitmap(bitmaps[i], positions[2*i], positions[(2*i)+1], null)
        }
        invalidate()
    }
}
