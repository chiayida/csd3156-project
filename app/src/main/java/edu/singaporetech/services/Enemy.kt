package edu.singaporetech.services


class Enemy(gameActivity: GameActivity) : Entity() {
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight: Float = (gameActivity.resources.displayMetrics.heightPixels).toFloat()
    private val length: Float = 200F

    val shoot: Shoot = Shoot(gameActivity,1000F, 0.5F, screenHeight, true)

    //private val imageView: ImageView

    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    init {
        // Initialise variables (Top-Middle of screen)
        position.x = screenWidth / 2F
        position.y = screenHeight / 8
        xScale = 4F
        yScale = 2F
        speed = 0.5F
        velocity.x = speed
        tag = "enemy"
        //velocity = 0.5F

        // Create an ImageView for the enemy (Placeholder for OpenGL texture)
        // Setting texture
        renderObject.setImageResource(R.drawable.coin)
        renderObject.xScale = xScale
        renderObject.yScale = yScale
        colliderScale = Vector2(100F, 100F)
        //gameActivity.addContentView(imageView, ViewGroup.LayoutParams(length.toInt(), length.toInt()))
    }
    fun updateShootMovement(dt: Float){
        shoot.updateMovement(dt)
    }
    fun update(dt: Float) {
        // Enemy bugs out (snapping?) occasionally when touching the sides of the screen.
        // Could be a ImageView issue, I am not too sure.
        if (position.x >= screenWidth)
        {
            velocity.x = -speed
        } else if (position.x <= 0F) {
            velocity.x = speed
        }

        // Update image's position
        renderObject.x = position.x
        renderObject.y = position.y

        // Shooting out projectile at delayed intervals
        shoot.update(dt, this, false)
    }
}
