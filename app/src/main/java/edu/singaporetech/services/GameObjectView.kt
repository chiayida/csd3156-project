package edu.singaporetech.services

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GameObjectView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var xPos: Float = 0f
    private var yPos: Float = 0f

    fun updatePosition(x: Float, y: Float) {
        xPos = x
        yPos = y
        invalidate()
    }
    fun getXPosition():Float
    {
        return xPos
    }
    fun getYPosition():Float
    {
        return yPos
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(xPos, yPos, 50f, Paint().apply { color = Color.RED })
    }
}