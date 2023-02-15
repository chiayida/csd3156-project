package edu.singaporetech.services

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView

class Projectile(private val gameActivity: GameActivity,
                 private val entity: Entity, private val _velocity: Float,
                 private val boundary: Float) : Entity() {
    private var flag: Boolean = _velocity > 0F
    //private val scale: Float = 100F

    //private val imageView: ImageView
    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    init {
        // Initialise variables (Bottom-Middle of screen)
        xPos = entity.xPos
        yPos = entity.yPos
        velocity = _velocity

        // Setting texture
        renderObject.setImageResource(R.drawable.coin)
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