package com.example.larble

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat

class BallView(context: Context) : View(context) {
    private val ballSrc: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ball2)
    private var ball : Bitmap = Bitmap.createScaledBitmap(ballSrc, 100, 100, true)
    var bitmaps: MutableList<Bitmap> = arrayListOf(ball)
    var positions= FloatArray(4){1.0f}
    private var paint: Paint = Paint()
    private val filter: ColorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_IN)
    var firstPaint: Paint = Paint()

    fun setParam(x: Float, y: Float) {
        positions[0] = x
        positions[1] = y
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(i in 0 until bitmaps.size){
            if(i==0){
                canvas.drawBitmap(bitmaps[i], positions[0], positions[1], firstPaint)
            }else{
                paint.colorFilter = filter
                paint.alpha = 50
                canvas.drawBitmap(bitmaps[i], positions[2*i], positions[(2*i)+1], paint)
            }
        }
        invalidate()
    }
}
