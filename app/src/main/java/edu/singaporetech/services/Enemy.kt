package edu.singaporetech.services

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView

class Enemy(private val gameActivity: GameActivity) : Entity() {
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight: Float = (gameActivity.resources.displayMetrics.heightPixels).toFloat()
    private val projectiles: MutableList<Projectile> = mutableListOf()
    private val length: Float = 200F

    private var projectileTimer: Float = 1000F
    private var projectileDelay: Float = 1000F

    private val imageView: ImageView


    init {
        // Initialise variables (Top-Middle of screen)
        xPos = screenWidth / 2F
        yPos = 0F
        velocity = 0.5F

        // Create an ImageView for the enemy (Placeholder for OpenGL texture)
        imageView = ImageView(gameActivity)
        imageView.setImageResource(R.drawable.coin)
        gameActivity.addContentView(imageView, ViewGroup.LayoutParams(length.toInt(), length.toInt()))
    }


    private fun updateShoot(dt: Float) {
        projectileTimer -= dt
        if (projectileTimer <= 0F) {
            val projectile = Projectile(gameActivity, xPos)
            projectiles.add(projectile)
            projectileTimer = projectileDelay
        }
    }


    fun update(dt: Float) {
        // Update position
        xPos += velocity * dt

        // Enemy bugs out (snapping?) occasionally when touching the sides of the screen.
        // Could be a ImageView issue, I am not too sure.
        if (xPos + length >= screenWidth || xPos <= 0F) {
            velocity = -velocity
        }

        // Update image's position (Placeholder for OpenGL texture)
        imageView.x = xPos
        imageView.y = yPos


        // Shooting out projectile at delayed intervals
        updateShoot(dt)


        // Updating projectiles
        for (projectile in projectiles) {
            projectile.update(dt)

            // Projectile out of bounds
            if (projectile.yPos > screenHeight) {
                // Might need to destroy the projectile, for now just remove from list
                projectiles.remove(projectile)
            }
        }
    }
}
