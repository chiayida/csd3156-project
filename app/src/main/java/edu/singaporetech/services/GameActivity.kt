package edu.singaporetech.services

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.pow


class GameActivity : AppCompatActivity(), SensorEventListener, OnGameEngineUpdate {
    val gameActivity: GameActivity = this

    //private lateinit var binding: ActivityGameBinding
    // ENGINE
    private var fpsCap = 1L
    private var engine = GameEngine(fpsCap, this)
    private var screenWidth: Float = 0F
    private var screenHeight: Float = 0f

    // SQL Database
    private lateinit var myRepository: MyRepository

    // GAME OBJECTS
    private lateinit var gamePlayer: Player
    private lateinit var gameEnemy: Enemy
    private lateinit var powerUp: PowerUp
    private var enemies: MutableList<Enemy> = mutableListOf()


    private val handler = Handler(Looper.getMainLooper())

    // GAME UI OBJECTS
    private lateinit var playerHealthView: TextView
    private lateinit var currentScoreView: TextView

    // PAUSE SCREEN OBJECTS
    private lateinit var pauseButton: Button
    private lateinit var returnMMButton: Button
    private lateinit var continueButton: Button
    private lateinit var restartButton: Button
    private lateinit var pauseText: TextView
    private lateinit var confirmationText: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private var isRestartShow = false
    private var isRestarting = false
    private var isDead = false

    // SOUNDS
    var soundSys = SoundSystem(this)


    // GAME LOGIC VARIABLES
    private lateinit var sensorManager: SensorManager
    private var aliveTime:Float = 0F
    private var powerUpBool: Boolean = false
    private var powerUpRespawnTimer: Float = 0f
    private var isShoot: Boolean = false

    //companion object {
    //    var screenWidth: Float = 0F
    //    var halfScreenWidth: Float = 0F
    //    var screenHeight: Float = 0F
    //    var halfScreenHeight: Float = 0F
    //}

    private val updateRunnable = object : Runnable {

        override fun run() {
            // Perform tasks here when the activity is updated
            engine.engineUpdate()
            handler.postDelayed(this, engine.updateInterval)
        }
    }
    private lateinit var gameCanvasView : GameCanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myRepository = MyRepository(this.applicationContext)

        screenWidth = (resources.displayMetrics.widthPixels).toFloat()
        //halfScreenWidth = screenWidth / 2F
        screenHeight = (resources.displayMetrics.heightPixels).toFloat()
        //halfScreenHeight = screenHeight / 2F

        GameSquare.clear()
        gameCanvasView = GameCanvasView(this)
        setContentView(gameCanvasView)

        // Initialize Gyroscope
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // INIT ALL SCREEN OBJECTS
        initViews()

        // Initialise game objects
        gameEnemy = Enemy(gameActivity)
        gamePlayer = Player(gameActivity)
        powerUp = PowerUp(gameActivity)

        val tempProjectile = Projectile(gameActivity, Vector2(0F, 0F),
            0F, 0F, ProjectileType.Enemy)

        GlobalScope.launch {
            // Enemies Data
            if (myRepository.getEnemiesData().isNotEmpty()) {
                val enemyData = myRepository.getEnemiesData()[0]
                gameEnemy.setDatabaseVariables(
                    Vector2(enemyData.positionX, enemyData.positionY),
                    enemyData.velocityX,
                    enemyData.projectileDamage,
                    enemyData.projectileDelay,
                    enemyData.projectileTimer,
                    enemyData.projectileVelocity,
                    enemyData.isAutoShoot,
                    enemyData.powerUpTimer
                )
            }
            myRepository.deleteAllEnemies()

            // Player Data
            if (myRepository.getPlayersData().isNotEmpty()) {
                val playerData = myRepository.getPlayersData()[0]
                gamePlayer.setDatabaseVariables(
                    Vector2(playerData.positionX, playerData.positionY),
                    playerData.velocityX,
                    playerData.score,
                    playerData.health,
                    playerData.projectileDamage,
                    playerData.projectileSpeed
                )
            }
            myRepository.deleteAllPlayers()

            // Projectiles Data
            if (myRepository.getProjectilesData().isNotEmpty()) {
                for (i in myRepository.getProjectilesData().indices) {
                    val projectileData = myRepository.getProjectilesData()[i]

                    tempProjectile.setDatabaseVariables(
                        Vector2(projectileData.positionX, projectileData.positionY),
                        projectileData.projectileVelocity,
                        projectileData.projectileBoundary,
                        projectileData.projectileType
                    )
                    val toBeAddedProjectile = tempProjectile.copy()

                    when (ProjectileType.values()[projectileData.projectileType]) {
                        ProjectileType.Player -> {
                            gamePlayer.shoot.projectiles.add(toBeAddedProjectile)
                        }
                        ProjectileType.Enemy -> {
                            gameEnemy.shoot.projectiles.add(toBeAddedProjectile)
                        }
                        else -> {
                            powerUp.shoot.projectiles.add(toBeAddedProjectile)
                        }
                    }
                }
                myRepository.deleteAllProjectiles()
            }
        }
        GameSquare.toBeDeleted.add(tempProjectile.renderObject)

        //Setting the GAME UI OBJECTS Values and position
        playerHealthView = TextView(this)
        playerHealthView.text = resources.getString(R.string._HealthUI, gamePlayer.health)
        playerHealthView.textSize = 25f
        playerHealthView.setTextColor(ContextCompat.getColor(this, R.color.text_color))
        playerHealthView.x = screenWidth * 0.3f
        playerHealthView.y = screenHeight * 0.065f
        playerHealthView.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(playerHealthView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        currentScoreView = TextView(this)
        currentScoreView.text = resources.getString(R.string._ScoreUI, gamePlayer.score)
        currentScoreView.textSize = 25f
        currentScoreView.setTextColor(ContextCompat.getColor(this, R.color.text_color))
        currentScoreView.x = screenWidth * 0.3f
        currentScoreView.y = screenHeight * 0.025f
        currentScoreView.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(currentScoreView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        // To shoot when player taps the screen
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnClickListener {
            isShoot = true
        }

        soundSys.initializeSounds()
        soundSys.playGameBGM()
        engine.engineInit()

        val flag = intent.getBooleanExtra("Resume", false)
        if(flag){
            engine.engineUpdate()
            engine.setPaused(true)
            togglePauseView(true)
        }
    }

    override fun onResume() {
        super.onResume()
        soundSys.playGameBGM()
        // Register the listener for the gyroscope sensor
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        handler.postDelayed(updateRunnable, engine.updateInterval)
    }

    override fun onStart() {
        super.onStart()
        soundSys.playGameBGM()
    }

    override fun onStop() {
        super.onStop()
        soundSys.stopSounds()
        soundSys.releaseSounds()
    }
    override fun onPause() {
        super.onPause()
        soundSys.stopSounds()
        engine.setPaused(true)
        if (!isDead) togglePauseView(true)
        // Unregister the listener
        sensorManager.unregisterListener(this)
        handler.removeCallbacks(updateRunnable)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("flag", true)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        if (!isRestarting && !isDead) {
            // Save data to database for it to be reloaded
            GlobalScope.launch {

                // Enemy Data
                val enemyData = EnemyData(
                    0,
                    gameEnemy.position.x,
                    gameEnemy.position.y,
                    gameEnemy.velocity.x,
                    gameEnemy.projectileDamage,
                    gameEnemy.shoot.projectileDelay,
                    gameEnemy.shoot.projectileTimer,
                    gameEnemy.shoot.projectileVelocity,
                    gameEnemy.shoot.isAutoShoot,
                    gameEnemy.shoot.powerUpTimer
                )
                myRepository.insertEnemyData(enemyData)

                for (projectile in gameEnemy.shoot.projectiles) {
                    val enemyProjectilesData = ProjectilesData(
                        0,
                        projectile.position.x,
                        projectile.position.y,
                        projectile.velocity.y,
                        projectile.projectileBoundary,
                        projectile.getProjectileType().ordinal
                    )
                    myRepository.insertProjectilesData(enemyProjectilesData)
                }

                // Player Data
                val playerData = PlayerData(
                    0,
                    gamePlayer.position.x,
                    gamePlayer.position.y,
                    gamePlayer.velocity.x,
                    gamePlayer.score,
                    gamePlayer.health,
                    gamePlayer.projectileDamage,
                    gamePlayer.projectileSpeed
                )
                myRepository.insertPlayerData(playerData)

                for (projectile in gamePlayer.shoot.projectiles) {
                    val playerProjectilesData = ProjectilesData(
                        0,
                        projectile.position.x,
                        projectile.position.y,
                        projectile.velocity.y,
                        projectile.projectileBoundary,
                        projectile.getProjectileType().ordinal
                    )
                    myRepository.insertProjectilesData(playerProjectilesData)
                }

                // PowerUp Data
                for (projectile in powerUp.shoot.projectiles) {
                    val powerUpProjectilesData = ProjectilesData(
                        0,
                        projectile.position.x,
                        projectile.position.y,
                        projectile.velocity.y,
                        projectile.projectileBoundary,
                        projectile.getProjectileType().ordinal
                    )
                    myRepository.insertProjectilesData(powerUpProjectilesData)
                }
            }
        }

        super.onDestroy()
    }

    //var directionSpeed:Float = 1.5f
    var currentOrientation:Float = 0.0f
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            // Get the three values for the gyroscope
            var y = event.values[1]
            if(y == -6.1086525E-4F || y == 6.1086525E-4F)
                y = 0F
            val deltaOrientationY = y * event.timestamp
            currentOrientation += deltaOrientationY
            Log.d("GyroO",currentOrientation.toString())
            if(currentOrientation > 0.1)
                gamePlayer.velocity.x = gamePlayer.speed
            else if (currentOrientation < -0.1)
                gamePlayer.velocity.x = -gamePlayer.speed
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun gameLogicInit(){
        enemies.add(gameEnemy)
    }

    override fun physicsInit(){
    }

    override fun onPhysicsUpdate(dt : Float){
        // COLLISION CHECK
        if(enemies.isNotEmpty()){
            for(k in enemies.indices) {
                val toBeDeleted: MutableList<Projectile> = mutableListOf()
                for(projectile in enemies[k].shoot.projectiles){
                    val projMIN = Vector2(projectile.getColliderMin().x , projectile.getColliderMin().y)
                    val projMAX = Vector2(projectile.getColliderMax().x , projectile.getColliderMax().y)
                    val projAABB = AABB(projMIN, projMAX)

                    val playerMIN = Vector2(gamePlayer.getColliderMin().x , gamePlayer.getColliderMin().y)
                    val playerMAX = Vector2(gamePlayer.getColliderMax().x , gamePlayer.getColliderMax().y)
                    val playerAABB = AABB(playerMIN, playerMAX)

                    if(Physics.collisionIntersectionRectRect(projAABB, projectile.velocity, playerAABB, gamePlayer.velocity, dt)){
                        //Bullet hit player
                        toBeDeleted.add(projectile)
                        if(gamePlayer.shieldDuration <= 0){
                            // Player will take damage if no shield
                            gamePlayer.health -= gameEnemy.projectileDamage
                            soundSys.playDamageSFX(true)
                        }
                        if (gamePlayer.health <= 0) {
                            GlobalScope.launch {
                                val score = HighscoreData(0, gamePlayer.score, aliveTime)
                                myRepository.insertHighscoreData(score)
                            }
                            isDead = true

                            val intent = Intent(this, HighscoreActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                for (projectile in toBeDeleted) {
                    GameSquare.toBeDeleted.add(projectile.renderObject)
                    enemies[k].shoot.projectiles.remove(projectile)
                }
            }
        }

        if(gamePlayer.shoot.projectiles.isNotEmpty()) {
            val toBeDeleted: MutableList<Projectile> = mutableListOf()
            for (projectile in gamePlayer.shoot.projectiles) {
                val projMIN = Vector2(projectile.getColliderMin().x, projectile.getColliderMin().y)
                val projMAX = Vector2(projectile.getColliderMax().x, projectile.getColliderMax().y)
                val projAABB = AABB(projMIN, projMAX)
                // else if it is the player's projectile, check collision with All ENEMIES
                if(enemies.isNotEmpty()) {
                    for (j in enemies.indices) {
                        val enemyMIN =
                            Vector2(enemies[j].getColliderMin().x, enemies[j].getColliderMin().y)
                        val enemyMAX =
                            Vector2(enemies[j].getColliderMax().x, enemies[j].getColliderMax().y)
                        val enemyAABB = AABB(enemyMIN, enemyMAX)
                        if (Physics.collisionIntersectionRectRect(
                                projAABB, projectile.velocity,
                                enemyAABB, enemies[j].velocity, dt
                            )
                        ){
                            toBeDeleted.add(projectile)
                            gamePlayer.score += gamePlayer.projectileDamage * 10
                            currentScoreView.text = resources.getString(R.string._ScoreUI, gamePlayer.score)
                            soundSys.playDamageSFX(false)
                        }
                    }
                }
            }
            for (projectile in toBeDeleted) {
                GameSquare.toBeDeleted.add(projectile.renderObject)
                gamePlayer.shoot.projectiles.remove(projectile)
            }
        }
        if(powerUp.shoot.projectiles.isNotEmpty()){
            val toBeDeleted: MutableList<Int> = mutableListOf()
            var index = 0
            for(powerUpProjectile in powerUp.shoot.projectiles){
                val projMIN = Vector2(powerUpProjectile.getColliderMin().x , powerUpProjectile.getColliderMin().y)
                val projMAX = Vector2(powerUpProjectile.getColliderMax().x , powerUpProjectile.getColliderMax().y)
                val projAABB = AABB(projMIN, projMAX)

                val playerMIN = Vector2(gamePlayer.getColliderMin().x , gamePlayer.getColliderMin().y)
                val playerMAX = Vector2(gamePlayer.getColliderMax().x , gamePlayer.getColliderMax().y)
                val playerAABB = AABB(playerMIN, playerMAX)

                if(Physics.collisionIntersectionRectRect(projAABB, powerUpProjectile.velocity, playerAABB, gamePlayer.velocity, dt)){
                    //Power up logic
                    toBeDeleted.add(index)
                    if(powerUpProjectile.getProjectileType() == ProjectileType.DamageBoost){
                        gamePlayer.projectileDamage += 1
                    }
                    else if(powerUpProjectile.getProjectileType() == ProjectileType.AddHealth)
                    {
                       gamePlayer.health += 1
                    }
                    else if(powerUpProjectile.getProjectileType() == ProjectileType.Shield)
                    {
                        gamePlayer.updatePlayerTexture(PlayerTexture.Shielded)
                        gamePlayer.shieldDuration = 10f
                    }
                    else if(powerUpProjectile.getProjectileType() == ProjectileType.SpeedBoost)
                    {
                        gamePlayer.updateProjectileSpeed(gamePlayer.projectileSpeed * 1.5f)
                    }
                }
                index++
            }
            for (i in toBeDeleted) {
                if(i < powerUp.shoot.projectiles.size){
                    GameSquare.toBeDeleted.add(powerUp.shoot.projectiles[i].renderObject)
                    powerUp.shoot.projectiles.removeAt(i)
                }
            }
        }
        // MOVEMENT UPDATE
        gamePlayer.updateProjectilesPosition(dt)
        powerUp.updateProjectilesPosition(dt)
        if(enemies.isNotEmpty()) {
            for (i in enemies.indices) {
                enemies[i].updateProjectilesPosition(dt)
                enemies[i].updatePosition(dt)
            }
        }
        gamePlayer.updatePosition(dt)
        powerUp.updatePosition(dt)
    }

    override fun onGameLogicUpdate(dt : Float){
        playerHealthView.text = resources.getString(R.string._HealthUI, gamePlayer.health)

        gameEnemy.update(dt)
        if(gamePlayer.shieldDuration > 0f) gamePlayer.shieldDuration -= dt/1000f
        aliveTime += dt/1000f
        powerUpRespawnTimer += dt/1000f
        if(gamePlayer.shieldDuration <= 0 && gamePlayer.texture != PlayerTexture.Default){
            //If no shield, return to Default Player Texture
            gamePlayer.updatePlayerTexture(PlayerTexture.Default)
        }
        //Power up spawn timer
        if(powerUpRespawnTimer >= 5f){
            powerUpBool = true
            powerUpRespawnTimer = 0f
        }
        //Enemy Gradually become stronger over time
        if(gamePlayer.score > gameEnemy.projectileDamage.toDouble().pow(2.0) * 100)
        {
            gameEnemy.projectileDamage += 1
            gameEnemy.updateEnemyProjectileSpeed(gameEnemy.enemyProjectileSpeed * 1.2f)
        }
        powerUp.update(dt,powerUpBool)
        powerUpBool = false
        gamePlayer.update(dt, isShoot)
        isShoot = false

        //// Create a new Path object to draw the square
        //val path = Path().apply {
        //    moveTo(100f, 100f)
        //    lineTo(200f, 100f)
        //    lineTo(200f, 200f)
        //    lineTo(100f, 200f)
        //    close()
        //}
        //// Draw the square with the texture
        //canvas.drawColor(Color.RED, PorterDuff.Mode.CLEAR)
        //canvas.drawPath(path, paint)
    }

    private fun initViews(){
        initPauseView()
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

    //UI Layout for Pause View
    private fun initPauseView(){
        pauseButton = Button(this)
        pauseButton.x = screenWidth * 0.05f
        pauseButton.y = pauseButton.x
        val size : Int = (screenWidth * 0.15f).toInt()
        pauseButton.setBackgroundResource(android.R.drawable.ic_media_pause)
        addContentView(pauseButton, ViewGroup.LayoutParams(size,size))
        pauseButton.setOnClickListener{
            soundSys.playClick()
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
        pauseText.text = resources.getString(R.string._PAUSED)
        pauseText.setTextColor(ContextCompat.getColor(this, R.color.text_color))
        pauseText.visibility = View.INVISIBLE
        pauseText.x = 0f
        pauseText.y = screenHeight * 0.3f
        pauseText.textSize = 90f
        pauseText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        pauseText.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(pauseText, ViewGroup.LayoutParams(screenWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT))

        restartButton = Button(this)
        val btWidth = (screenWidth * 0.4f).toInt()
        val btHeight = (btWidth * 0.3f).toInt()
        restartButton.x = screenWidth * 0.5f - btWidth * 0.5f
        restartButton.y = screenHeight * 0.53f
        restartButton.text = resources.getString(R.string._RESTART)
        restartButton.textSize = 30f
        restartButton.visibility = View.INVISIBLE
        restartButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        restartButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(restartButton, ViewGroup.LayoutParams(btWidth, btHeight))
        restartButton.setOnClickListener{
            isRestartShow = true
            confirmationText.text = resources.getString(R.string._ConfirmRestart)
            toggleConfirmationView(true)
            togglePauseView(false)
            soundSys.playClick()
        }

        returnMMButton = Button(this)
        returnMMButton.x = screenWidth * 0.5f - btWidth * 0.5f
        returnMMButton.y = screenHeight * 0.61f
        returnMMButton.text =  resources.getString(R.string._MainMenu)
        returnMMButton.textSize = 30f
        returnMMButton.visibility = View.INVISIBLE
        returnMMButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        returnMMButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(returnMMButton, ViewGroup.LayoutParams(btWidth, btHeight))
        returnMMButton.setOnClickListener{
            isRestartShow = false
            confirmationText.text = resources.getString(R.string._ConfirmMMenu)
            toggleConfirmationView(true)
            togglePauseView(false)
            soundSys.playClick()
        }

        continueButton = Button(this)
        continueButton.x = screenWidth * 0.5f - btWidth * 0.5f
        continueButton.y = screenHeight * 0.45f
        continueButton.text = resources.getString(R.string._ContinueBtn)
        continueButton.textSize = 30f
        continueButton.visibility = View.INVISIBLE
        continueButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        continueButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(continueButton, ViewGroup.LayoutParams(btWidth, btHeight))
        continueButton.setOnClickListener{
            togglePauseView(false)
            engine.setPaused(false)
            soundSys.playClick()
        }

        yesButton = Button(this)
        yesButton.x = screenWidth * 0.5f - btWidth * 0.5f
        yesButton.y = screenHeight * 0.5f
        yesButton.text = resources.getString(R.string._YES)
        yesButton.textSize = 30f
        yesButton.visibility = View.INVISIBLE
        yesButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        yesButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(yesButton, ViewGroup.LayoutParams(btWidth, btHeight))
        yesButton.setOnClickListener{
            soundSys.playClick()
            toggleConfirmationView(false)
            if(isRestartShow){
                isRestarting = true
                recreate()
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("flag", true)
                startActivity(intent)
                finish()
            }
        }

        noButton = Button(this)
        noButton.x = screenWidth * 0.5f - btWidth * 0.5f
        noButton.y = screenHeight * 0.58f
        noButton.text = resources.getString(R.string._NO)
        noButton.textSize = 30f
        noButton.visibility = View.INVISIBLE
        noButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        noButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(noButton, ViewGroup.LayoutParams(btWidth, btHeight))
        noButton.setOnClickListener{
            togglePauseView(true)
            toggleConfirmationView(false)
            soundSys.playClick()
        }

        confirmationText = TextView(this)
        confirmationText.text = resources.getString(R.string._Confirm)
        confirmationText.textSize = 40f
        confirmationText.setTextColor(ContextCompat.getColor(this, R.color.text_color))
        confirmationText.visibility = View.INVISIBLE
        confirmationText.x = 0f
        confirmationText.y = screenHeight * 0.375f
        confirmationText.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        confirmationText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        addContentView(confirmationText, ViewGroup.LayoutParams(screenWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    // UNUSED Functions
    /*
    private val alpha = 0.8f // Filter coefficient
    private var lastValues = floatArrayOf(0f, 0f, 0f)


    private fun applyFilteringTechniques(values: FloatArray): FloatArray {
        val filteredValues = FloatArray(3)

        // Apply a low-pass filter to the raw data
        filteredValues[0] = alpha * lastValues[0] + (1 - alpha) * values[0]
        filteredValues[1] = alpha * lastValues[1] + (1 - alpha) * values[1]
        filteredValues[2] = alpha * lastValues[2] + (1 - alpha) * values[2]

        // Save the filtered values for the next iteration
        lastValues = filteredValues

        return filteredValues
    }
    */
}