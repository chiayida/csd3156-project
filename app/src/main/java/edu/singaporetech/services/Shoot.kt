package edu.singaporetech.services

import kotlin.random.Random

class Shoot(private val gameActivity: GameActivity,
             var projectileDelay: Float, var projectileVelocity: Float,
            private val projectileBoundary: Float, var isAutoShoot: Boolean,
            private var projectileType : ProjectileType) {

    var projectiles: MutableList<Projectile> = mutableListOf()
    var projectileTimer: Float = projectileDelay
    var powerUpTimer: Float = 0F
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()


    fun setDatabaseVariables(projectileDelay_: Float, projectileTimer_: Float,
                             projectileVelocity_: Float, isAutoShoot_: Boolean, powerUpTimer_: Float) {
        projectileDelay = projectileDelay_
        projectileTimer = projectileTimer_
        projectileVelocity = projectileVelocity_
        isAutoShoot = isAutoShoot_
        powerUpTimer = powerUpTimer_
    }


    fun updatePositions(dt: Float) {
        for (projectile in projectiles) {
            projectile.updatePosition(dt)
        }
    }
    fun updateSpeed(speed:Float)
    {
        projectileVelocity = speed
    }

    fun update(dt: Float, entity: Entity, isClickToShoot: Boolean) {
        projectileTimer -= dt
        if (projectileTimer <= 0F) {
            if (isAutoShoot || isClickToShoot) {
                val projectile = Projectile(gameActivity, entity.position,
                    projectileVelocity, projectileBoundary, projectileType)
                projectiles.add(projectile)
                projectileTimer = projectileDelay
                gameActivity.soundSys.playShootSFX(projectileType)
            }
        }
        // Update projectiles
        val toBeDeleted: MutableList<Projectile> = mutableListOf()
        for (projectile in projectiles) {
            if (!projectile.update()) {
                toBeDeleted.add(projectile)
            }
        }
        for (projectile in toBeDeleted) {
            projectiles.remove(projectile)
        }
    }


    fun updatePowerUp(dt: Float, entity: Entity, powerBool: Boolean) {
        powerUpTimer -= dt
        if (powerUpTimer <= 0F) {
            if(powerBool)
            {
                //randomize the position
                val pos = Vector2(entity.position.x , entity.position.y)
                val random = Random.Default
                val randomX = random.nextFloat() * screenWidth
                pos.x = randomX

                // Randomize the power up
                val possiblePowerups = listOf(ProjectileType.DamageBoost, ProjectileType.AddHealth,
                    ProjectileType.Shield,ProjectileType.SpeedBoost)
                projectileType = possiblePowerups.random()
                val projectile = Projectile(gameActivity, pos,
                    projectileVelocity, projectileBoundary, projectileType)
                projectiles.add(projectile)
            }
        }
        val toBeDeleted: MutableList<Projectile> = mutableListOf()
        for (projectile in projectiles) {
            if (!projectile.update()) {
                toBeDeleted.add(projectile)
            }
        }
        for (projectile in toBeDeleted) {
            projectiles.remove(projectile)
        }
    }
}
