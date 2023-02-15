package edu.singaporetech.services


enum class ProjectileType {
    ENEMY,
    PLAYER,
    NONE
}


class Projectile(gameActivity: GameActivity,
                 shooterPosition: Vector2, _velocity: Float,
                 private val projectileBoundary: Float): Entity() {
    private var flag: Boolean = _velocity > 0F
    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)
    private val projectileDamage: Int = 1

    init {
        position = Vector2(shooterPosition.x, shooterPosition.y)
        colliderScale = Vector2(50F, 50F)
        speed = _velocity
        velocity.y = speed

        // Setting texture
        renderObject.setImageResource(R.drawable.coin)
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
    }


    fun update(): Boolean {
        if ((getColliderMin().y >= projectileBoundary && flag) ||
            (getColliderMax().y <= projectileBoundary && !flag)) {
            GameGLSquare.toBeDeleted.add(renderObject)
            return false
        }

        // Update position
        renderObject.x = position.x
        renderObject.y = position.y

        return true
    }
}