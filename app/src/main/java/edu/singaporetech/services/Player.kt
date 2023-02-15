package edu.singaporetech.services

import java.lang.Float.max
import java.lang.Float.min

class Player(gameActivity: GameActivity) : Entity() {
    private val screenWidth = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight = (gameActivity.resources.displayMetrics.heightPixels).toFloat()

    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    private val minXPos: Float = 50f
    private val maxXPos: Float = (screenWidth - 50f) // width is the width of the screen
    private val minYPos: Float = 50f
    private val maxYPos: Float = (screenHeight - 50f) // height is the height of the screen

    val shoot: Shoot = Shoot(gameActivity,1000F, -0.5F, 0F, false)

    init {
        position = Vector2(screenWidth / 2F, screenHeight - 50f)
        colliderScale = Vector2(100F, 100F)
        speed = 0.5F
        tag = "player"

        // Setting texture
        renderObject.setImageResource(R.drawable.coin)
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
    }


    fun updatePosition(x: Float, y: Float) {
        position.x = max(minXPos, min(x, maxXPos))
        position.y = max(minYPos, min(y, maxYPos))
    }


    fun updateShootMovement(dt: Float) {
        shoot.updateMovement(dt)
    }


    fun update(dt: Float, isShoot: Boolean) {
        shoot.update(dt, this, isShoot)

        // Setting texture
        renderObject.x = position.x
        renderObject.y = position.y
    }
}