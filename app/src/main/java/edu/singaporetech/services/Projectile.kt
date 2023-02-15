package edu.singaporetech.services


class Projectile(gameActivity: GameActivity,
                 entity: Entity, _velocity: Float,
                 private val boundary: Float) : Entity() {
                 private var flag: Boolean = _velocity > 0F
    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    init {
        xPos = entity.xPos
        yPos = entity.yPos
        xScale = 1F
        yScale = 1F
        velocity = _velocity

        // Setting texture
        renderObject.setImageResource(R.drawable.coin)
        renderObject.xScale = xScale
        renderObject.yScale = yScale
    }


    fun update(dt: Float): Boolean {
        // Update position
        yPos += velocity * dt

        // Check if the projectile is out of bounds.
        if ((yPos >= boundary && flag) || (yPos <= boundary && !flag)) {
            GameGLSquare.toBeDeleted.add(renderObject)
            return false
        }

        // Update image's position
        renderObject.x = xPos
        renderObject.y = yPos

        return true
    }
}