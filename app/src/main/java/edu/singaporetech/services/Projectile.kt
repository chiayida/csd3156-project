package edu.singaporetech.services

enum class ProjectileType{
    Player,
    Enemy,
    DamageBoost,
    AddHealth,
    Shield,
    SpeedBoost
}

class Projectile(val gameActivity: GameActivity, position_: Vector2, velocity_: Float,
                var projectileBoundary: Float, projectileType_: ProjectileType): Entity() {
    private var flag: Boolean = velocity_ > 0F
    val renderObject: GameSquare = GameSquare(gameActivity)

    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight: Float = (gameActivity.resources.displayMetrics.heightPixels).toFloat()
    private val powerUpSize: Float = screenWidth * 0.04f
    private val bulletSize: Float = powerUpSize * 0.6f

    private var projectileType = projectileType_

    init {
        position = Vector2(position_.x, position_.y)
        speed = velocity_
        velocity.y = speed

        // Setting texture
        when (projectileType) {
            ProjectileType.Player -> {
                colliderScale = Vector2(bulletSize, bulletSize)
                renderObject.setImageResource(R.drawable.player_bullet)
            }
            ProjectileType.Enemy -> {
                colliderScale = Vector2(bulletSize, bulletSize)
                renderObject.setImageResource(R.drawable.enemy_bullet)
            }
            ProjectileType.DamageBoost -> {
                colliderScale = Vector2(powerUpSize, powerUpSize)
                renderObject.setImageResource(R.drawable.damage)
            }
            ProjectileType.AddHealth -> {
                colliderScale = Vector2(powerUpSize, powerUpSize)
                renderObject.setImageResource(R.drawable.health)
            }
            ProjectileType.Shield -> {
                colliderScale = Vector2(powerUpSize, powerUpSize)
                renderObject.setImageResource(R.drawable.shield)
            }
            ProjectileType.SpeedBoost -> {
                colliderScale = Vector2(powerUpSize, powerUpSize)
                renderObject.setImageResource(R.drawable.speed)
            }
        }
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
    }


    fun copy(): Projectile {
        return Projectile(gameActivity, position, velocity.y, projectileBoundary, projectileType)
    }


    fun getProjectileType():ProjectileType {
        return projectileType
    }


    fun setDatabaseVariables(position_: Vector2, velocity_: Float,
                             projectileBoundary_: Float, type: Int) {
        position = position_
        speed = velocity_
        velocity.y = speed
        flag = velocity_ > 0F

        projectileBoundary = projectileBoundary_

        projectileType = ProjectileType.values()[type]
        when (projectileType) {
            ProjectileType.Player -> {
                colliderScale = Vector2(35F, 35F)
                renderObject.setImageResource(R.drawable.player_bullet)
            }
            ProjectileType.Enemy -> {
                colliderScale = Vector2(35F, 35F)
                renderObject.setImageResource(R.drawable.enemy_bullet)
            }
            ProjectileType.DamageBoost -> {
                colliderScale = Vector2(50F, 50F)
                renderObject.setImageResource(R.drawable.player)
            }
            ProjectileType.AddHealth -> {
                colliderScale = Vector2(50F, 50F)
                renderObject.setImageResource(R.drawable.enemy)
            }
            ProjectileType.Shield -> {
                colliderScale = Vector2(50F, 50F)
                renderObject.setImageResource(R.drawable.coin)
            }
            ProjectileType.SpeedBoost -> {
                colliderScale = Vector2(50F, 50F)
                renderObject.setImageResource(R.drawable.coin)
            }
        }
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
    }



    fun update(): Boolean {
        if ((getColliderMin().y >= projectileBoundary && flag) ||
            (getColliderMax().y <= projectileBoundary && !flag)) {
            GameSquare.toBeDeleted.add(renderObject)
            return false
        }

        // Update position
        renderObject.x = position.x
        renderObject.y = position.y

        return true
    }
}