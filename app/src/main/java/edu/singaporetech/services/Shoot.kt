package edu.singaporetech.services

import android.util.Log

class Shoot(private val gameActivity: GameActivity,
            private val projectileDelay: Float, private val shootVelocity: Float,
            private val boundary: Float, private val isAutoShoot: Boolean) {
    private val projectiles: MutableList<Projectile> = mutableListOf()
    private var projectileTimer: Float = projectileDelay


    fun update(dt: Float, entity: Entity, isShooting: Boolean) {
        projectileTimer -= dt

        if (isAutoShoot && projectileTimer <= 0F) {
            val projectile = Projectile(gameActivity, entity, shootVelocity, boundary)
            projectiles.add(projectile)
            projectileTimer = projectileDelay
        }
        //else if (isShooting && projectileTimer <= 0F) {
        //    val projectile = Projectile(gameActivity, entity, shootVelocity, boundary)
        //    projectiles.add(projectile)
        //    projectileTimer = projectileDelay
        //}


        // Updating projectiles
        val toBeDeleted: MutableList<Projectile> = mutableListOf()
        for (projectile in projectiles) {
            if (projectile.update(dt) == false) {

                // Projectile out of bounds
                // Might need to destroy the projectile, and remove from list
                toBeDeleted.add(projectile)
            }
        }
        for (projectile in toBeDeleted) {
            projectiles.remove(projectile)
        }
    }
}