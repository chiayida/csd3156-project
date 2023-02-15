package edu.singaporetech.services

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView

class Projectile(private val gameActivity: GameActivity,
                 private val entity: Entity, private val _velocity: Float,
                 private val boundary: Float) : Entity() {
    private var length: Float = 100F
    private var flag: Boolean = _velocity > 0F

    //private val imageView: ImageView
    private val imageView: GameGLSquare

    init {
        // Initialise variables (Bottom-Middle of screen)
        xPos = entity.xPos
        yPos = entity.yPos
        velocity = _velocity

        // Create an ImageView for the projectile (Placeholder for OpenGL texture)
        //imageView = ImageView(gameActivity)
        imageView = GameGLSquare(gameActivity)
        imageView.setImageResource(R.drawable.coin)
        //gameActivity.addContentView(imageView, ViewGroup.LayoutParams(length.toInt(), length.toInt()))
    }


    fun update(dt: Float): Boolean {
        // Update position
        yPos += velocity * dt

        // Check if the projectile is out of bounds.
        // Currently imageView is not removed, it will crash the app (idky).
        if ((yPos >= boundary && flag) || (yPos <= boundary && !flag)) {
            // Hacky method to prevent crashing but memory will keep increasing.
            // Memory is not deleted, at top of screen lol xD
            yPos = length
            velocity = 0F

            GameGLSquare.toBeDeleted.add(imageView)

            return false
        }

        // Update image's position (Placeholder for OpenGL texture)
        imageView.x = xPos
        imageView.y = yPos

        return true
    }
}