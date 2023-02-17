package edu.singaporetech.services

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import edu.singaporetech.services.databinding.ActivityGameBinding


class GameActivity : AppCompatActivity(), SensorEventListener, OnGameEngineUpdate {

    val TAG: String = "GameActivity"
    private lateinit var binding: ActivityGameBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var gamePlayer: Player
    private lateinit var gameEnemy: Enemy
    private lateinit var powerUp: PowerUp
    private var Enemies: MutableList<Enemy> = mutableListOf()

    private var FPSCap = 1L
    private var engine = GameEngine(FPSCap, this)

    private val handler = Handler()

   // private lateinit var fpsView: TextView
   // private lateinit var dtView: TextView

    private lateinit var playerHealthView: TextView
    private lateinit var enemyHealthView: TextView

    // PAUSE SCREEN OBJECTS
    private lateinit var pauseButton: Button
    private lateinit var returnMMButton: Button
    private lateinit var continueButton: Button
    private lateinit var restartButton: Button
    private lateinit var pauseText: TextView
    private lateinit var confirmationText: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    var isRestart = false

    private var direction:Float = 0.0f
    private var offsetBottom:Float = 250.0f

    private var screenWidth: Float = 0F
    private var screenHeight: Float = 0f

    var directionSpeed:Float = 1.5f
    var currentOrientation:Float = 0.0f
    var score:Int = 0
    var scoreCounter:Int = 0
    var powerUpBool: Boolean = false
    var sheildBool: Boolean = false

    private var isShoot: Boolean = false

    companion object {
        var screenWidth: Float = 0F
        var halfScreenWidth: Float = 0F
        var screenHeight: Float = 0F
        var halfScreenHeight: Float = 0F

        private lateinit var gLView: GameGLSurfaceView
    }

    private val updateRunnable = object : Runnable {

        override fun run() {
            // Perform tasks here when the activity is updated
            engine.EngineUpdate()

            if (engine.getFPSUpdated()) {
               // fpsView.text = "${engine.getFPS()} FPS"
               // dtView.text = "${engine.getDeltaTime()}ms dt"
            }

            handler.postDelayed(this, engine.updateInterval)
        }
    }

    /*
    *
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screenWidth = (resources.displayMetrics.widthPixels).toFloat()
        halfScreenWidth = screenWidth / 2F
        screenHeight = (resources.displayMetrics.heightPixels).toFloat()
        halfScreenHeight = screenHeight / 2F

        GameGLSquare.Clear()
        // Initialize view binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        gLView = GameGLSurfaceView(this)
        //setContentView(binding.root)
        setContentView(gLView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // INIT ALL SCREEN OBJECTS
        initViews()

        Log.d(TAG,resources.displayMetrics.heightPixels.toFloat().toString())

        gamePlayer = Player(this)
        gameEnemy = Enemy(this)
        powerUp = PowerUp(this)

        playerHealthView = TextView(this)
        playerHealthView.text = "Player Health: " + gamePlayer.health
        playerHealthView.textSize = 24f
        playerHealthView.setTextColor(resources.getColor(R.color.text_color))
        playerHealthView.x = 600f
        playerHealthView.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(playerHealthView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        enemyHealthView = TextView(this)
        enemyHealthView.text = "Enemy Health: " + gameEnemy.health
        enemyHealthView.textSize = 24f
        enemyHealthView.setTextColor(resources.getColor(R.color.text_color))
        enemyHealthView.x = 600f
        enemyHealthView.y = 100f
        enemyHealthView.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(enemyHealthView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        direction = 0F

        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnClickListener {
            isShoot = true
        }
        engine.EngineInit()
    }

    /*
    *
    * */
    override fun onResume() {
        super.onResume()
        // Register the listener for the gyroscope sensor
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        handler.postDelayed(updateRunnable, engine.updateInterval)
    }

    /*
    *
    * */
    override fun onPause() {
        super.onPause()
        engine.setPaused(true)
        togglePauseView(true)
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
        return super.onTouchEvent(event)
    }

    /*
    *
    * */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            // Get the three values for the gyroscope
            val x = event.values[0]
            val z = event.values[2]
            gamePlayer.velocity.x = x * gamePlayer.speed
        }
    }

    /*
    *
    * */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
        //    Log.w(TAG, "Sensor accuracy changed to UNRELIABLE")
        //} else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
        //    Log.w(TAG, "Sensor accuracy changed to LOW")
        //} else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) {
        //    Log.d(TAG, "Sensor accuracy changed to MEDIUM")
        //} else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
        //    Log.d(TAG, "Sensor accuracy changed to HIGH")
        //}
    }

    override fun GameLogicInit(){
        Enemies.add(gameEnemy)
    }

    override fun PhysicsInit(){
    }

    override fun OnPhysicsUpdate(dt : Float){
        // COLLISION CHECK
        if(Enemies.isNotEmpty()){
            for(k in Enemies.indices) {
                val toBeDeleted: MutableList<Projectile> = mutableListOf()
                for(projectile in Enemies[k].shoot.projectiles){
                    var projMIN = Vector2(projectile.getColliderMin().x , projectile.getColliderMin().y)
                    var projMAX = Vector2(projectile.getColliderMax().x , projectile.getColliderMax().y)
                    var projAABB = AABB(projMIN, projMAX)

                    var playerMIN = Vector2(gamePlayer.getColliderMin().x , gamePlayer.getColliderMin().y)
                    var playerMAX = Vector2(gamePlayer.getColliderMax().x , gamePlayer.getColliderMax().y)
                    var playerAABB = AABB(playerMIN, playerMAX)

                    if(Physics.collisionIntersectionRectRect(projAABB, projectile.velocity, playerAABB, gamePlayer.velocity, dt)){
                        //Bullet hit player
                        toBeDeleted.add(projectile)
                        if(sheildBool){
                            sheildBool = false
                            gamePlayer.updatePlayerTexture(R.drawable.player)
                        }
                        else{
                            gamePlayer.health -= gameEnemy.projectileDamage
                        }
                        playerHealthView.text = "Player Health: " + gamePlayer.health

                        if (gamePlayer.health <= 0) {
                            // TODO Go to Lose/Win screen
                            //val intent = Intent(this, GameActivity::class.java)
                            //startActivity(intent)
                        }
                    }
                }
                for (projectile in toBeDeleted) {
                    GameGLSquare.toBeDeleted.add(projectile.renderObject)
                    Enemies[k].shoot.projectiles.remove(projectile)
                }
            }
        }
        if(gamePlayer.shoot.projectiles.isNotEmpty()) {
            val toBeDeleted: MutableList<Projectile> = mutableListOf()
            for (projectile in gamePlayer.shoot.projectiles) {
                var projMIN = Vector2(projectile.getColliderMin().x, projectile.getColliderMin().y)
                var projMAX = Vector2(projectile.getColliderMax().x, projectile.getColliderMax().y)
                var projAABB = AABB(projMIN, projMAX)
                // else if it is the player's projectile, check collision with All ENEMIES
                if(Enemies.isNotEmpty()) {
                    for (j in Enemies.indices) {
                        var enemyMIN =
                            Vector2(Enemies[j].getColliderMin().x, Enemies[j].getColliderMin().y)
                        var enemyMAX =
                            Vector2(Enemies[j].getColliderMax().x, Enemies[j].getColliderMax().y)
                        var enemyAABB = AABB(enemyMIN, enemyMAX)
                        if (Physics.collisionIntersectionRectRect(
                                projAABB, projectile.velocity,
                                enemyAABB, Enemies[j].velocity, dt
                            )
                        ) {
                            //Bullet Hit enemy removed cause change to endless
                            toBeDeleted.add(projectile)
                            //Score Counter logic to be changed later just for testing
                            scoreCounter += 1
                            score += gamePlayer.projectileDamage * 10
                        }
                    }
                }
            }
            for (projectile in toBeDeleted) {
                GameGLSquare.toBeDeleted.add(projectile.renderObject)
                gamePlayer.shoot.projectiles.remove(projectile)
            }
        }
        if(powerUp.shoot.projectiles.isNotEmpty()){
            val toBeDeleted: MutableList<Projectile> = mutableListOf()
            for(powerUpProjectile in powerUp.shoot.projectiles){
                var projMIN = Vector2(powerUpProjectile.getColliderMin().x , powerUpProjectile.getColliderMin().y)
                var projMAX = Vector2(powerUpProjectile.getColliderMax().x , powerUpProjectile.getColliderMax().y)
                var projAABB = AABB(projMIN, projMAX)

                var playerMIN = Vector2(gamePlayer.getColliderMin().x , gamePlayer.getColliderMin().y)
                var playerMAX = Vector2(gamePlayer.getColliderMax().x , gamePlayer.getColliderMax().y)
                var playerAABB = AABB(playerMIN, playerMAX)

                if(Physics.collisionIntersectionRectRect(projAABB, powerUpProjectile.velocity, playerAABB, gamePlayer.velocity, dt)){
                    //Power up logic
                    toBeDeleted.add(powerUpProjectile)
                    if(powerUpProjectile.getProjectileType() == ProjectileType.DamageBoost)
                        gamePlayer.projectileDamage += 1
                    if(powerUpProjectile.getProjectileType() == ProjectileType.AddHealth)
                        gamePlayer.health += 1
                    if(powerUpProjectile.getProjectileType() == ProjectileType.Sheild)
                    {
                        gamePlayer.updatePlayerTexture(R.drawable.player_bullet)
                        sheildBool = true
                    }
                }
                for (projectile in toBeDeleted) {
                    GameGLSquare.toBeDeleted.add(projectile.renderObject)
                    powerUp.shoot.projectiles.remove(powerUpProjectile)
                }
            }
        }
        // MOVEMENT UPDATE
        gamePlayer.updateProjectilesPosition(dt)
        powerUp.updateProjectilesPosition(dt)
        if(Enemies.isNotEmpty()) {
            for (i in Enemies.indices) {
                Enemies[i].updateProjectilesPosition(dt)
                Enemies[i].updatePosition(dt)
            }
        }
        gamePlayer.updatePosition(dt)
        powerUp.updatePosition(dt)
        //gamePlayer.updatePosition(gamePlayer.position.x + direction
        //    ,resources.displayMetrics.heightPixels.toFloat() - offsetBottom)
    }


    override fun OnGameLogicUpdate(dt : Float){
        gameEnemy.update(dt)

        if(scoreCounter > 6){
            powerUpBool = true
            scoreCounter -= 6
        }
        if(score > 100)
            gameEnemy.projectileDamage += 1
//        var entity = Entity()
//        entity.position.x = gamePlayer.position.x - 50f
//        entity.position.y = gamePlayer.position.y
        powerUp.update(dt,powerUpBool)
        powerUpBool = false
        gamePlayer.update(dt, isShoot)
        isShoot = false
    }

    private fun initViews(){
        initPauseView()
        //        fpsView = TextView(this)
//        fpsView.text = "Hello, world!"
//        fpsView.textSize = 24f
//        fpsView.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
//        addContentView(fpsView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

//        dtView = TextView(this)
//        dtView.text = " delta time"
//        dtView.textSize = 24f
//        dtView.y = 100f
//        dtView.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
//        addContentView(dtView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun togglePauseView(show : Boolean){
        if(show){
            pauseButton.setBackgroundResource(android.R.drawable.ic_media_play)
            pauseText.visibility = View.VISIBLE
            restartButton.visibility = View.VISIBLE
            returnMMButton.visibility = View.VISIBLE
            continueButton.visibility = View.VISIBLE
        }
        else{
            pauseButton.setBackgroundResource(android.R.drawable.ic_media_pause)
            pauseText.visibility = View.INVISIBLE
            restartButton.visibility = View.INVISIBLE
            returnMMButton.visibility = View.INVISIBLE
            continueButton.visibility = View.INVISIBLE
        }
    }

    private fun toggleConfirmationView(show : Boolean){
        if(show){
            confirmationText.visibility = View.VISIBLE
            yesButton.visibility = View.VISIBLE
            noButton.visibility = View.VISIBLE
        }
        else{
            confirmationText.visibility = View.INVISIBLE
            yesButton.visibility = View.INVISIBLE
            noButton.visibility = View.INVISIBLE
        }
    }

    private fun initPauseView(){
        pauseButton = Button(this)
        pauseButton.x = 50f
        pauseButton.y = 0f
        pauseButton.setBackgroundResource(android.R.drawable.ic_media_pause)
        addContentView(pauseButton, ViewGroup.LayoutParams(250, 250))
        pauseButton.setOnClickListener{
            if (!engine.getPaused()){
                togglePauseView(true)
                engine.setPaused(true)
            }
            else{
                togglePauseView(false)
                engine.setPaused(false)
            }
        }

        pauseText = TextView(this)
        pauseText.text = "PAUSED"
        pauseText.textSize = 90f
        pauseText.setTextColor(resources.getColor(R.color.text_color))
        pauseText.visibility = View.INVISIBLE
        pauseText.x = 0f
        pauseText.y = 920f
        pauseText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        pauseText.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(pauseText, ViewGroup.LayoutParams(screenWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT))

        restartButton = Button(this)
        restartButton.x = (screenWidth / 2f) - 250f
        restartButton.y = 1500f
        restartButton.text = "RESTART"
        restartButton.textSize = 30f
        restartButton.visibility = View.INVISIBLE
        restartButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        restartButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(restartButton, ViewGroup.LayoutParams(500, 150))
        restartButton.setOnClickListener{
            isRestart = true
            confirmationText.text = "Confirm Restart the game?"
            toggleConfirmationView(true)
            togglePauseView(false)
        }

        returnMMButton = Button(this)
        returnMMButton.x = (screenWidth / 2f) - 250f
        returnMMButton.y = 1700f
        returnMMButton.text = "Main Menu"
        returnMMButton.textSize = 30f
        returnMMButton.visibility = View.INVISIBLE
        returnMMButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        returnMMButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(returnMMButton, ViewGroup.LayoutParams(500, 150))
        returnMMButton.setOnClickListener{
            isRestart = false
            confirmationText.text = "Confirm return to the Main Menu?"
            toggleConfirmationView(true)
            togglePauseView(false)
        }

        continueButton = Button(this)
        continueButton.x = (screenWidth / 2f) - 250f
        continueButton.y = 1300f
        continueButton.text = "CONTINUE"
        continueButton.textSize = 30f
        continueButton.visibility = View.INVISIBLE
        continueButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        continueButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(continueButton, ViewGroup.LayoutParams(500, 150))
        continueButton.setOnClickListener{
            togglePauseView(false)
            engine.setPaused(false)
        }

        yesButton = Button(this)
        yesButton.x = (screenWidth / 2f) - 250f
        yesButton.y = 1400f
        yesButton.text = "YES"
        yesButton.textSize = 30f
        yesButton.visibility = View.INVISIBLE
        yesButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        yesButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(yesButton, ViewGroup.LayoutParams(500, 150))
        yesButton.setOnClickListener{
            if(isRestart){
                recreate()
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }

        noButton = Button(this)
        noButton.x = (screenWidth / 2f) - 250f
        noButton.y = 1600f
        noButton.text = "NO"
        noButton.textSize = 30f
        noButton.visibility = View.INVISIBLE
        noButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        noButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(noButton, ViewGroup.LayoutParams(500, 150))
        noButton.setOnClickListener{
            togglePauseView(true)
            toggleConfirmationView(false)
        }

        confirmationText = TextView(this)
        confirmationText.text = "CONFIRM?"
        confirmationText.textSize = 40f
        confirmationText.setTextColor(resources.getColor(R.color.text_color))
        confirmationText.visibility = View.INVISIBLE
        confirmationText.x = 0f
        confirmationText.y = 1100f
        confirmationText.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        confirmationText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        addContentView(confirmationText, ViewGroup.LayoutParams(screenWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT))
    }
}