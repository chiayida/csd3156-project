package edu.singaporetech.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), SensorEventListener, OnGameEngineUpdate {

    val TAG: String = "GameActivity"
    private lateinit var binding: ActivityGameBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var gameObjectView: GameObjectView

    private var FPSCap = 1L
    private var engine = GameEngine(FPSCap, this)
    private lateinit var gameEnemy: Enemy

    private val handler = Handler()

    private lateinit var fpsView: TextView
    private lateinit var dtView: TextView
    private var direction:Float = 0.0f
    private var offsetBottom:Float = 250.0f

    private val updateRunnable = object : Runnable {


        override fun run() {
            // Perform tasks here when the activity is updated
            engine.EngineUpdate()

            if (engine.getFPSUpdated()) {
                //Log.d("Game:", "Game is Running at $frames fps")
                fpsView?.text = "${engine.getFPS()} FPS"
                dtView?.text = "${engine.getDeltaTime()}s dt"
            }
            /*Log.d("ObjPos",gameObjectView.getXPosition().toString())
            Log.d("ObjPos",gameObjectView.getYPosition().toString())*/
            gameObjectView.updatePosition(gameObjectView.getXPosition() + direction
                ,resources.displayMetrics.heightPixels.toFloat() - offsetBottom)
            handler.postDelayed(this, engine.updateInterval)
        }
    }

    //private lateinit var gLView: GameGLSurfaceView
    /*
    *
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //gLView = GameGLSurfaceView(this)
        //setContentView(gLView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // FIND ALL SCREEN OBJECTS
        gameObjectView =  binding.gameObject1
        fpsView =   binding.textViewFPS
        dtView =  binding.textViewDeltaTime

        Log.d(TAG,resources.displayMetrics.heightPixels.toFloat().toString())
        gameObjectView.updatePosition(resources.displayMetrics.widthPixels.toFloat()/2
            ,resources.displayMetrics.heightPixels.toFloat() - offsetBottom)
        direction = 0F

        gameEnemy = Enemy(this)
    }

    /*
    *
    * */
    override fun onResume() {
        super.onResume()
        // Register the listener for the gyroscope sensor
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        handler.postDelayed(updateRunnable, engine.updateInterval)
    }

    /*
    *
    * */
    override fun onPause() {
        super.onPause()
        // Unregister the listener
        sensorManager.unregisterListener(this)
        handler.removeCallbacks(updateRunnable)
    }

    /*
    *
    * */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i("Mouse","X: ${event.x}, Y: ${event.y} ")
        val touchX = event.x
        val touchY = event.y
        gameObjectView.updatePosition(touchX, touchY)
        return super.onTouchEvent(event)
    }

    /*
    *
    * */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            // Get the three values for the gyroscope
            val x = event.values[0]
            val y = event.values[1]
            Log.d("Sensor x",x.toString())
            Log.d("Sensor y",y.toString())
            // Shift the gameobj base on Y rot which is the x direction
            if(y > 0.5)
            {
                direction = 4.0f
            }
            if(y < -0.5)
            {
                direction = -4.0f
            }
            /*if(y < 0.5f && y > -0.5)
            {
                direction = 0.0f
            }*/
            /*if(y.toInt() != 0)
            {
                event?.let {
                    gameObjectView.updatePosition(gameObjectView.getXPosition() + y * 100
                        ,resources.displayMetrics.heightPixels.toFloat() - 300.0f)
                }
            }*/
        }
    }

    /*
    *
    * */
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

    override fun GameLogicInit(){

    }
    override fun PhysicsInit(){

    }
    override fun OnPhysicsUpdate(dt : Float){

    }
    override fun OnGameLogicUpdate(dt : Float){
        gameEnemy.update(dt)
    }

}