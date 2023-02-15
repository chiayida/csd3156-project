package edu.singaporetech.services


class Enemy(private val gameActivity: GameActivity) : Entity() {
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight: Float = (gameActivity.resources.displayMetrics.heightPixels).toFloat()

    private val speed: Float = 0.5F

    private val shoot: Shoot = Shoot(gameActivity,1000F, 0.5F, screenHeight, true)
    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    init {
        // Initialise variables (Top-Middle of screen)
        xPos = screenWidth / 2
        yPos = screenHeight / 8
        xScale = 4F
        yScale = 2F

        velocity = speed

        // Setting texture
        renderObject.setImageResource(R.drawable.coin)
        renderObject.xScale = xScale
        renderObject.yScale = yScale
    }


    fun update(dt: Float) {
        // Update position
        xPos += velocity * dt

        // Update velocity
        if (xPos >= screenWidth) {
            velocity = -speed
        } else if (xPos <= 0F) {
            velocity = speed
        }

        // Update image's position
        renderObject.x = xPos
        renderObject.y = yPos

        // Shooting out projectile at delayed intervals
        shoot.update(dt, this, false)
    }
}
