package edu.singaporetech.services

import android.util.Log
import kotlin.random.Random

class Shoot(private val gameActivity: GameActivity,
            private val projectileDelay: Float, private val projectileVelocity: Float,
            private val projectileBoundary: Float, private val isAutoShoot: Boolean,
            private var projectileType : ProjectileType) {
    val projectiles: MutableList<Projectile> = mutableListOf()
    private var projectileTimer: Float = projectileDelay
    private var powerUpTimer: Float = 0f
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    fun updatePositions(dt: Float) {
        for (projectile in projectiles) {
            projectile.updatePosition(dt)
        }
    }


    fun update(dt: Float, entity: Entity, isClickToShoot: Boolean) {
        projectileTimer -= dt
        if (projectileTimer <= 0F) {
            if (isAutoShoot || isClickToShoot) {
                val projectile = Projectile(gameActivity, entity.position,
                    projectileVelocity, projectileBoundary, projectileType)
                projectiles.add(projectile)
                projectileTimer = projectileDelay
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
    fun updatePowerUp(dt: Float, entity: Entity, powerBool: Boolean)
    {
        powerUpTimer -= dt
        if (powerUpTimer <= 0F) {
            if(powerBool)
            {
                //randomize the position
                var pos = Vector2(entity.position.x , entity.position.y)
                val random = Random.Default
                val randomX = random.nextFloat() * screenWidth
                pos.x = randomX
                //randomize the powerup
                val possiblePowerups = listOf(ProjectileType.DamageBoost, ProjectileType.AddHealth, ProjectileType.Sheild)
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
