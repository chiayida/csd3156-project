package edu.singaporetech.services
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class GameActivity : AppCompatActivity(), SensorEventListener {
    val TAG: String = "GameActivity"
    private lateinit var sensorManager: SensorManager
    private lateinit var gyroscopeSensor: Sensor
    private lateinit var gameObjectView: GameObjectView

    private val handler = Handler()
    private val updateInterval = 16L // update interval in milliseconds

    private val updateRunnable = object : Runnable {
        var previousTime = System.currentTimeMillis()
        var fpsTime = System.currentTimeMillis()
        var deltaTime: Float = 0f
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
        Log.d(TAG,"Calling onCreate")
        setContentView(R.layout.activity_game)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gameObjectView = findViewById(R.id.game_object_view)
        // TODO: GAME LOGIC HERE

    }
    override fun onResume() {
        super.onResume()
        // Register the listener for the gyroscope sensor
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        handler.postDelayed(updateRunnable, updateInterval)
    }
    override fun onPause() {
        super.onPause()
        // Unregister the listener
        sensorManager.unregisterListener(this)
        handler.removeCallbacks(updateRunnable)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            // Get the three values for the gyroscope
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            Log.d("Sensor x",x.toString())
            Log.d("Sensor y",y.toString())
            Log.d("Sensor z",z.toString())
            // Do something with the values, e.g. update a text view
            event?.let {
                gameObjectView.updatePosition(gameObjectView.getXPosition() + event.values[0] * 100,
                    gameObjectView.getYPosition() + event.values[1] * 100)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.w(TAG, "Sensor accuracy changed to UNRELIABLE")
        } else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
            Log.w(TAG, "Sensor accuracy changed to LOW")
        } else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) {
            Log.d(TAG, "Sensor accuracy changed to MEDIUM")
        } else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            Log.d(TAG, "Sensor accuracy changed to HIGH")
        }
    }

    private fun onUpdate(dt : Float) {
        // Perform tasks here when the activity is updated
        Log.d("Game:", "Game is Running at ${dt.toBigDecimal()} seconds per frame")
    }

}