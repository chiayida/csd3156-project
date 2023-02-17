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

    val shoot: Shoot = Shoot(gameActivity,1000F, -0.5F, 0F, false, ProjectileType.Player)
    val projectileDamage: Int = 1
    var health: Int = 5


    init {
        colliderScale = Vector2(100F, 100F)
        speed = 0.025F

        // Setting texture
        renderObject.setImageResource(R.drawable.player)
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y

        position = Vector2(screenWidth / 2F, screenHeight - renderObject.yScale)
    }

    override fun updatePosition(dt : Float) {
        position.x = max(minXPos, min( position.x + velocity.x * dt, maxXPos))
        position.y = max(minYPos, min(position.y + velocity.y * dt, maxYPos))
    }

    fun updateProjectilesPosition(dt: Float) {
        shoot.updatePositions(dt)
    }


    fun update(dt: Float, isShoot: Boolean) {
        shoot.update(dt, this, isShoot)

        // Setting texture
        renderObject.x = position.x
        renderObject.y = position.y
    }
}