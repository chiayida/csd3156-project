package edu.singaporetech.services

import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView

@Suppress("DEPRECATION")
class Enemy(private val gameActivity: GameActivity) {
    private val imageView: ImageView

    companion object {
        private var xPos: Float = 0F
        private var yPos: Float = 0F
        private var halfExtents: Float = 200F
        private var speed: Float = 1F
    }

    init {
        // Get the window manager to retrieve the screen width and height
        val windowManager = gameActivity.getSystemService(WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val screenWidth = Point()
        display.getSize(screenWidth)

        // Initialise variables (Top-Middle of screen)
        xPos = (screenWidth.x / 2).toFloat()

        // Create an ImageView for the enemy
        imageView = ImageView(gameActivity)
        imageView.setImageResource(R.drawable.coin)
        gameActivity.addContentView(imageView,
            ViewGroup.LayoutParams(200, 200))
    }

    fun update(dt : Float) {
        xPos += speed * dt

        if (xPos - halfExtents >= gameActivity.resources.displayMetrics.widthPixels) {
            speed = -speed
        } else if (xPos + halfExtents <= 0) {
            speed = -speed
        }

        // Update image's position
        imageView.x = xPos
        imageView.y = yPos
    }
}
