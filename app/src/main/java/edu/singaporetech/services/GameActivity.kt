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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class GameActivity : AppCompatActivity(), SensorEventListener, OnGameEngineUpdate {
    val gameActivity: GameActivity = this
    // SQL Database
    private lateinit var myRepository: MyRepository

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
    var isRestart = false
    var isDead = false

    // SOUNDS
    var soundSys = SoundSystem(this)


    private var direction:Float = 0.0f

    private var screenWidth: Float = 0F
    private var screenHeight: Float = 0f

    var aliveTime:Float = 0F
    var scoreCounter:Int = 0
    var powerUpBool: Boolean = false
    var sheildBool: Float = 0f

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

            // Debug fps and delta time
            //if (engine.getFPSUpdated()) {
               // fpsView.text = "${engine.getFPS()} FPS"
               // dtView.text = "${engine.getDeltaTime()}ms dt"
            //}
            handler.postDelayed(this, engine.updateInterval)
        }
    }

    /*
    *
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myRepository = MyRepository(this.applicationContext)

        screenWidth = (resources.displayMetrics.widthPixels).toFloat()
        halfScreenWidth = screenWidth / 2F
        screenHeight = (resources.displayMetrics.heightPixels).toFloat()
        halfScreenHeight = screenHeight / 2F

        GameGLSquare.Clear()
        // Initialize view binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        gLView = GameGLSurfaceView(this)
        setContentView(gLView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // INIT ALL SCREEN OBJECTS
        initViews()

        // Initialise game objects
        gameEnemy = Enemy(gameActivity)
        gamePlayer = Player(gameActivity)
        powerUp = PowerUp(gameActivity)

        var tempProjectile = Projectile(gameActivity, Vector2(0F, 0F),
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
                    var toBeAddedProjectile = tempProjectile.copy()

                    when (ProjectileType.values()[projectileData.projectileType]) {
                        ProjectileType.Player -> {
                            gamePlayer.shoot.projectiles.add(toBeAddedProjectile)
                            Log.d("type", "Player")
                        }
                        ProjectileType.Enemy -> {
                            gameEnemy.shoot.projectiles.add(toBeAddedProjectile)
                            Log.d("type", "Enemy")
                        }
                        else -> {
                            powerUp.shoot.projectiles.add(toBeAddedProjectile)
                            Log.d("type", "power up")
                        }
                    }
                }
                myRepository.deleteAllProjectiles()
            }
        }
        GameGLSquare.toBeDeleted.add(tempProjectile.renderObject)


        playerHealthView = TextView(this)
        playerHealthView.text = "Player Health: " + gamePlayer.health
        playerHealthView.textSize = 24f
        playerHealthView.setTextColor(resources.getColor(R.color.text_color))
        playerHealthView.x = 550f
        playerHealthView.y = 150f
        playerHealthView.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(playerHealthView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        currentScoreView = TextView(this)
        currentScoreView.text = "Current score: " + gamePlayer.score
        currentScoreView.textSize = 24f
        currentScoreView.setTextColor(resources.getColor(R.color.text_color))
        currentScoreView.x = 550f
        currentScoreView.y = 50f
        currentScoreView.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(currentScoreView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        direction = 0F

        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnClickListener {
            isShoot = true
        }

        soundSys.InitializeSounds()
        soundSys.playGameBGM()
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
        soundSys.StopSounds()
        engine.setPaused(true)
        if (!isDead) togglePauseView(true)
        // Unregister the listener
        sensorManager.unregisterListener(this)
        handler.removeCallbacks(updateRunnable)
    }
    /*
    *
    * */
    override fun onDestroy() {
        if (!isRestart) {
            // Save data to database for it to be reloaded
            GlobalScope.launch {

                // Enemy Data
                var enemyData = EnemyData(
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
                    var enemyProjectilesData = ProjectilesData(
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
                var playerData = PlayerData(
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
                    var playerProjectilesData = ProjectilesData(
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
                    var powerUpProjectilesData = ProjectilesData(
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
        soundSys.ReleaseSounds()
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
                        if(sheildBool < 0){
                            //Default Player Texture and will take damage
                            gamePlayer.updatePlayerTexture(2)
                            gamePlayer.health -= gameEnemy.projectileDamage
                        }
                        soundSys.playDamageSFX(true)
                        if (gamePlayer.health <= 0) {
                            GlobalScope.launch {
                                val score = HighscoreData(0, gamePlayer.score, aliveTime)
                                myRepository.insertHighscoreData(score)
                            }

                            isDead = true

                            val intent = Intent(this, HighscoreActivity::class.java)
                            startActivity(intent)
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
                            gamePlayer.score += gamePlayer.projectileDamage * 10
                            currentScoreView.text = "Current score: " + gamePlayer.score
                            soundSys.playDamageSFX(false)
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
                    {
                        if(gamePlayer.health < 5)
                            gamePlayer.health += 1
                    }
                    if(powerUpProjectile.getProjectileType() == ProjectileType.Shield)
                    {
                        gamePlayer.updatePlayerTexture(1)
                        sheildBool = 20f
                    }
                    if(powerUpProjectile.getProjectileType() == ProjectileType.SpeedBoost)
                    {
                        gamePlayer.updateProjectileSpeed(gamePlayer.projectileSpeed * 1.5f)
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
        playerHealthView.text = "Player Health: " + gamePlayer.health

        gameEnemy.update(dt)
        sheildBool -= dt/1000f
        aliveTime += dt/1000f
        //Every 10 hit player will get a power up
        if(scoreCounter > 10){
            powerUpBool = true
            scoreCounter -= 10
        }
        //Enemy Gradually become stronger over time
        if(gamePlayer.score > Math.pow(gameEnemy.projectileDamage.toDouble(), 2.0) * 100)
        {
            gameEnemy.projectileDamage += 1
            gameEnemy.updateEnemyProjectileSpeed(gameEnemy.EnemyProjectileSpeed * 1.2f)
        }
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
        pauseButton.y = 25f
        pauseButton.setBackgroundResource(android.R.drawable.ic_media_pause)
        addContentView(pauseButton, ViewGroup.LayoutParams(250, 250))
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
        pauseText.text = "PAUSED"
        pauseText.textSize = 90f
        pauseText.setTextColor(resources.getColor(R.color.text_color))
        pauseText.visibility = View.INVISIBLE
        pauseText.x = 0f
        pauseText.y = (screenHeight / 2f) - 500f
        pauseText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        pauseText.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(pauseText, ViewGroup.LayoutParams(screenWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT))

        restartButton = Button(this)
        restartButton.x = (screenWidth / 2f) - 250f
        restartButton.y = (screenHeight / 2f) + 100f
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
            soundSys.playClick()
        }

        returnMMButton = Button(this)
        returnMMButton.x = (screenWidth / 2f) - 250f
        returnMMButton.y = (screenHeight / 2f) + 300f
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
            soundSys.playClick()
        }

        continueButton = Button(this)
        continueButton.x = (screenWidth / 2f) - 250f
        continueButton.y = (screenHeight / 2f) - 100f
        continueButton.text = "CONTINUE"
        continueButton.textSize = 30f
        continueButton.visibility = View.INVISIBLE
        continueButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        continueButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(continueButton, ViewGroup.LayoutParams(500, 150))
        continueButton.setOnClickListener{
            togglePauseView(false)
            engine.setPaused(false)
            soundSys.playClick()
        }

        yesButton = Button(this)
        yesButton.x = (screenWidth / 2f) - 250f
        yesButton.y = (screenHeight / 2f)
        yesButton.text = "YES"
        yesButton.textSize = 30f
        yesButton.visibility = View.INVISIBLE
        yesButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        yesButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(yesButton, ViewGroup.LayoutParams(500, 150))
        yesButton.setOnClickListener{
            soundSys.playClick()
            toggleConfirmationView(false)
            if(isRestart){
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
        noButton.x = (screenWidth / 2f) - 250f
        noButton.y = (screenHeight / 2f) + 200f
        noButton.text = "NO"
        noButton.textSize = 30f
        noButton.visibility = View.INVISIBLE
        noButton.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame)
        noButton.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        addContentView(noButton, ViewGroup.LayoutParams(500, 150))
        noButton.setOnClickListener{
            togglePauseView(true)
            toggleConfirmationView(false)
            soundSys.playClick()
        }

        confirmationText = TextView(this)
        confirmationText.text = "CONFIRM?"
        confirmationText.textSize = 40f
        confirmationText.setTextColor(resources.getColor(R.color.text_color))
        confirmationText.visibility = View.INVISIBLE
        confirmationText.x = 0f
        confirmationText.y = (screenHeight / 2f) - 300f
        confirmationText.typeface = ResourcesCompat.getFont(this, R.font.aldotheapache)
        confirmationText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        addContentView(confirmationText, ViewGroup.LayoutParams(screenWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT))
    }
}