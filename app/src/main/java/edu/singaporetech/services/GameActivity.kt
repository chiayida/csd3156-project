package edu.singaporetech.services

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class GameActivity : AppCompatActivity() {

    private val handler = Handler()
    private val updateInterval = 16L // update interval in milliseconds

    private val updateRunnable = object : Runnable {
        var previousTime = System.currentTimeMillis()
        var fpsTime = System.currentTimeMillis()
        var deltaTime : Float = 0f
        var frames = 0

        override fun run() {
            // Perform tasks here when the activity is updated
            frames++
            if (System.currentTimeMillis() - fpsTime >= 1000) {
                Log.d("Game:", "Game is Running at $frames fps")
                frames = 0
                fpsTime = System.currentTimeMillis()
            }
            deltaTime = (System.currentTimeMillis() - previousTime) / 1000f
            previousTime = System.currentTimeMillis()
            onUpdate(deltaTime)
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // TODO: GAME LOGIC HERE

    }

    private fun onUpdate(dt : Float) {
        // Perform tasks here when the activity is updated
        Log.d("Game:", "Game is Running at ${dt.toBigDecimal()} seconds per frame")
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(updateRunnable, updateInterval)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
    }

}