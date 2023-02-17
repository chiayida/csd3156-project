package edu.singaporetech.services

class PowerUp(gameActivity: GameActivity):Entity() {
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight: Float = (gameActivity.resources.displayMetrics.heightPixels).toFloat()
    val shoot: Shoot = Shoot(gameActivity,1000F, 0.5F, screenHeight, true, ProjectileType.DamageBoost)
    val renderObject: GameGLSquare = GameGLSquare(gameActivity)

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

    fun updateProjectilesPosition(dt: Float) {
        shoot.updatePositions(dt)
    }

    fun update(dt: Float , powerBool:Boolean) {
        shoot.updatePowerUp(dt, this, powerBool)
        // Setting texture
        renderObject.x = position.x
        renderObject.y = position.y
    }
}