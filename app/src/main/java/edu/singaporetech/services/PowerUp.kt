package edu.singaporetech.services

import java.lang.Float.max

class PowerUp(gameActivity: GameActivity):Entity() {
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight: Float = (gameActivity.resources.displayMetrics.heightPixels).toFloat()

    private val minXPos: Float = 50f
    private val maxXPos: Float = (screenWidth - 50f) // width is the width of the screen
    val shoot: Shoot = Shoot(gameActivity,1000F, 0.5F, screenHeight, true, ProjectileType.DamageBoost)
    private val renderObject: GameSquare = GameSquare(gameActivity)

    // Initializing of entity variables
    init {
        position = Vector2(screenWidth / 2, -100.0f)
        colliderScale = Vector2(50F, 50F)
        speed = 0.0F
        velocity.y = speed

        // Setting texture
        renderObject.setImageResource(R.drawable.player_bullet)
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
    }

    //Update the position of the projectiles
    fun updateProjectilesPosition(dt: Float) {
        shoot.updatePositions(dt)
    }

    //Update the logic for the Power up projectiles to shoot as well as the rendering of the Power up
    fun update(dt: Float , powerBool:Boolean) {
        shoot.updatePowerUp(dt, this, powerBool)
        position.x = max(minXPos, java.lang.Float.min(position.x + velocity.x * dt, maxXPos))
        renderObject.x = position.x
        renderObject.y = position.y
    }
}