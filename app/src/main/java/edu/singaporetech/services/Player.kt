package edu.singaporetech.services

import java.lang.Float.max
import java.lang.Float.min

enum class PlayerTexture{
    Default,
    Shielded
}


class Player(gameActivity: GameActivity) : Entity() {
    private val screenWidth = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight = (gameActivity.resources.displayMetrics.heightPixels).toFloat()
    private val renderObject: GameSquare = GameSquare(gameActivity)

    //Boundaries to prevent the player from moving out of the screen.
    private val size: Float = screenWidth * 0.07f
    private val minXPos: Float = size
    private val maxXPos: Float = (screenWidth - size)
    private val minYPos: Float = size
    private val maxYPos: Float = (screenHeight - size)

    var projectileDamage: Int = 1
    var texture: PlayerTexture = PlayerTexture.Default
    var health: Int = 5
    var projectileSpeed = -0.5f
    var shieldDuration = 0.0f
    var score: Int = 0
    val shoot: Shoot = Shoot(gameActivity,500F, projectileSpeed, 0F, false, ProjectileType.Player)

    //Initialization of the Entity variables
    init {
        colliderScale = Vector2(size, size)
        speed = 0.5F

        // Setting texture
        renderObject.setImageResource(R.drawable.player)
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
        renderObject.x = 300f
        position = Vector2(screenWidth / 2F, screenHeight / 1.2F)
    }

    //Storing the players data into the database
    fun setDatabaseVariables(position_: Vector2, velocity_: Float, score_: Int, health_: Int,
                             projectileDamage_: Int, projectileSpeed_: Float) {
        position = Vector2(position_.x, position_.y)
        velocity.x = velocity_
        score = score_
        health = health_
        projectileDamage = projectileDamage_
        projectileSpeed = projectileSpeed_
    }

    //Changing of textures
    fun updatePlayerTexture(_texture : PlayerTexture)
    {
        if(_texture == PlayerTexture.Shielded)
        {
            renderObject.setImageResource(R.drawable.shield_player)
        }
       else if(_texture == PlayerTexture.Default)
        {
            renderObject.setImageResource(R.drawable.player)
        }
       texture = _texture
    }

    //Update the position of the player
    override fun updatePosition(dt : Float) {
        position.x = max(minXPos, min( position.x + velocity.x * dt, maxXPos))
        position.y = max(minYPos, min(position.y + velocity.y * dt, maxYPos))
    }

    //Update the position of the player's projectiles
    fun updateProjectilesPosition(dt: Float) {
        shoot.updatePositions(dt)
    }

    //Update the projectile speed when power up is obtained.
    fun updateProjectileSpeed(speed:Float)
    {
        projectileSpeed = speed
        shoot.updateSpeed(speed)
    }

    //Update the logic for the projectiles to shoot as well as the rendering of the player
    fun update(dt: Float, isShoot: Boolean) {
        shoot.update(dt, this, isShoot)
        renderObject.x = position.x
        renderObject.y = position.y
    }
}