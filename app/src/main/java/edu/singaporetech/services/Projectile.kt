package edu.singaporetech.services


enum class ProjectileType {
    ENEMY,
    PLAYER,
}

class Projectile(private val gameActivity: GameActivity,
                 private val shooterPosition: Vector2, private val _velocity: Float,
                 private val boundary: Float, val type : ProjectileType) : Entity() {
    private var length: Float = 100F
    private var flag: Boolean = _velocity > 0F

    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    init {
        // Initialise variables (Bottom-Middle of screen)
        position.x = shooterPosition.x
        position.y = shooterPosition.y
        xScale = 1F
        yScale = 1F
        speed = _velocity
        velocity.y = speed

        // Setting texture
        renderObject.setImageResource(R.drawable.coin)
        renderObject.xScale = xScale
        renderObject.yScale = yScale
        colliderScale = Vector2(100F, 100F)
        //gameActivity.addContentView(imageView, ViewGroup.LayoutParams(length.toInt(), length.toInt()))
    }


    fun update(dt: Float): Boolean {

        // Check if the projectile is out of bounds.
        // Currently imageView is not removed, it will crash the app (idky).
        if ((position.y >= boundary && flag) || (position.y <= boundary && !flag)) {
            // Hacky method to prevent crashing but memory will keep increasing.
            // Memory is not deleted, at top of screen lol xD
            position.y = length
            velocity.toZero()

            GameGLSquare.toBeDeleted.add(renderObject)

            return false
        }

        // Update image's position
        renderObject.x = position.x
        renderObject.y = position.y

        return true
    }
}