package edu.singaporetech.services

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import java.lang.Float.max
import java.lang.Float.min

class Player(private val gameActivity: GameActivity) : Entity() {

    private val screenWidth = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight = (gameActivity.resources.displayMetrics.heightPixels).toFloat()
    private val paint = Paint()
    //private val textureBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.sit_logo)
    private val imageView: GameGLSquare = GameGLSquare(gameActivity)

    private val minXPos: Float = 50f
    private val maxXPos: Float = (screenWidth - 50f) // width is the width of the screen
    private val minYPos: Float = 50f
    private val maxYPos: Float = (screenHeight - 50f) // height is the height of the screen

    val shoot: Shoot = Shoot(gameActivity,1000F, -0.5F, screenHeight, true)

    init{
        // Initialise variables (Top-Middle of screen)
        position.x = screenWidth / 2F
        position.y = screenHeight - 50f
        speed = 0.5F
        tag = "player"
        imageView.setImageResource(R.drawable.coin)
        colliderScale = Vector2(100F, 100F)
    }
    fun updatePosition(x: Float, y: Float) {
        position.x = max(minXPos, min(x, maxXPos))
        position.y = max(minYPos, min(y, maxYPos))
        /*invalidate()*/
    }

    fun updateShootMovement(dt: Float){
        shoot.updateMovement(dt)
    }
    fun update(dt: Float, isShoot: Boolean) {
        shoot.update(dt, this, isShoot)
        // Update image's position (Placeholder for OpenGL texture)
        imageView.x = position.x
        imageView.y = position.y
    }
   /* override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        *//*canvas?.drawColor(Color.WHITE) // clear canvas to white

        // set up the paint object to use the texture bitmap
        val shader = BitmapShader(textureBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        paint.shader = shader

        // draw a rectangle with the texture
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas?.drawRect(rect, paint)*//*
        canvas?.drawCircle(xPos, yPos, 50f, Paint().apply { color = Color.RED })
    }*/
}