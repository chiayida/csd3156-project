package edu.singaporetech.services

enum class ProjectileType{
    Player,
    Enemy
}


class Projectile(gameActivity: GameActivity,
                 shooterPosition: Vector2, _velocity: Float,
                 private val projectileBoundary: Float, type : ProjectileType): Entity() {
    private var flag: Boolean = _velocity > 0F
    val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    init {
        position = Vector2(shooterPosition.x, shooterPosition.y)
        colliderScale = Vector2(50F, 50F)
        speed = _velocity
        velocity.y = speed

        // Setting texture
        if(type == ProjectileType.Player){
            renderObject.setImageResource(R.drawable.player_bullet)
        }
        else{
            renderObject.setImageResource(R.drawable.enemy_bullet)
        }
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