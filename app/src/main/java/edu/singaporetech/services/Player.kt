package edu.singaporetech.services

import android.util.Log
import java.lang.Float.max
import java.lang.Float.min

enum class PlayerTexture{
    default,
    shielded
}


class Player(gameActivity: GameActivity) : Entity() {
    private val screenWidth = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight = (gameActivity.resources.displayMetrics.heightPixels).toFloat()

    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    var score: Int = 0

    private val minXPos: Float = 50f
    private val maxXPos: Float = (screenWidth - 50f) // width is the width of the screen
    private val minYPos: Float = 50f
    private val maxYPos: Float = (screenHeight - 50f) // height is the height of the screen

    var projectileDamage: Int = 1
    var texture: PlayerTexture = PlayerTexture.default
    var health: Int = 5
    var projectileSpeed = -0.5f
    var shieldDuration = 0.0f
    val shoot: Shoot = Shoot(gameActivity,500F, projectileSpeed, 0F, false, ProjectileType.Player)


    init {
        colliderScale = Vector2(100F, 100F)
        speed = 0.025F

        // Setting texture
        renderObject.setImageResource(R.drawable.player)
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y

        position = Vector2(screenWidth / 2F, screenHeight - 500f)
    }

    fun setDatabaseVariables(position_: Vector2, velocity_: Float, score_: Int, health_: Int,
                             projectileDamage_: Int, projectileSpeed_: Float) {
        position = Vector2(position_.x, position_.y)
        velocity.x = velocity_
        score = score_
        health = health_
        projectileDamage = projectileDamage_
        projectileSpeed = projectileSpeed_
    }

    fun updatePlayerTexture(_texture : PlayerTexture)
    {
        if(_texture == PlayerTexture.shielded)
        {
            renderObject.setImageResource(R.drawable.shield_player)
        }
       else if(_texture == PlayerTexture.default)
        {
            renderObject.setImageResource(R.drawable.player)
        }
       texture = _texture
    }
    override fun updatePosition(dt : Float) {
        position.x = max(minXPos, min( position.x + velocity.x * dt, maxXPos))
        position.y = max(minYPos, min(position.y + velocity.y * dt, maxYPos))
    }

    fun updateProjectilesPosition(dt: Float) {
        shoot.updatePositions(dt)
    }

    fun updateProjectileSpeed(speed:Float)
    {
        projectileSpeed = speed
        shoot.UpdateSpeed(speed)
    }
    fun update(dt: Float, isShoot: Boolean) {
        shoot.update(dt, this, isShoot)

        // Setting texture
        renderObject.x = position.x
        renderObject.y = position.y
    }
}