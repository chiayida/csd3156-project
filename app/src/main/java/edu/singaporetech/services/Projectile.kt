package edu.singaporetech.services

import android.view.ViewGroup
import android.widget.ImageView

class Projectile(private val gameActivity: GameActivity, private val _xPos: Float) : Entity() {
    private val screenHeight = gameActivity.resources.displayMetrics.heightPixels.toFloat()
    private val imageView: ImageView
    private var length: Float = 100F


    init {
        // Initialise variables (Bottom-Middle of screen)
        xPos = _xPos
        yPos = 0F
        velocity = 0.5F

        // Create an ImageView for the projectile (Placeholder for OpenGL texture)
        imageView = ImageView(gameActivity)
        imageView.setImageResource(R.drawable.coin)
        gameActivity.addContentView(imageView, ViewGroup.LayoutParams(length.toInt(), length.toInt())
        )
    }

    fun update(dt: Float) {
        // Update position
        yPos += velocity * dt

        // Check if the projectile is out of bounds.
        // Currently imageView is not removed, it will crash the app (idky).
        if (yPos >= screenHeight) {
            // Hacky method to prevent crashing but memory will keep increasing.
            // removed from list but memory is not deleted, at top of screen lols xD
            yPos = 0F - length
            velocity = 0F
        }

        // Update image's position (Placeholder for OpenGL texture)
        imageView.x = xPos
        imageView.y = yPos
    }
}