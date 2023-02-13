package edu.singaporetech.services

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import java.lang.Float.max
import java.lang.Float.min

class GameObjectView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    private val screenWidth = displayMetrics.widthPixels
    private val screenHeight = displayMetrics.heightPixels

    private var xPos: Float = 0f
    private var yPos: Float = 0f
    private val minXPos: Float = 50f
    private val maxXPos: Float = (screenWidth - 50f) // width is the width of the screen
    private val minYPos: Float = 50f
    private val maxYPos: Float = (screenHeight - 50f) // height is the height of the screen

    fun updatePosition(x: Float, y: Float) {
        xPos = max(minXPos, min(x, maxXPos))
        yPos = max(minYPos, min(y, maxYPos))
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