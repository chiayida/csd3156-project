package edu.singaporetech.services


class Shoot(private val gameActivity: GameActivity,
            private val projectileDelay: Float, private val projectileVelocity: Float,
            private val projectileBoundary: Float, private val isAutoShoot: Boolean,
            private val projectileType : ProjectileType) {
    val projectiles: MutableList<Projectile> = mutableListOf()
    private var projectileTimer: Float = projectileDelay


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
}