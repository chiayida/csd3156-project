package edu.singaporetech.services


class Shoot(private val gameActivity: GameActivity,
            private val projectileDelay: Float, private val projectileVelocity: Float,
            private val projectileBoundary: Float, private val isAutoShoot: Boolean) {
    private val projectiles: MutableList<Projectile> = mutableListOf()
    private var projectileTimer: Float = projectileDelay


    fun update(dt: Float, entity: Entity, isClickToShoot: Boolean) {
        projectileTimer -= dt

        if (projectileTimer <= 0F) {
            if (isAutoShoot || isClickToShoot) {
                val projectile = Projectile(gameActivity, entity, projectileVelocity, projectileBoundary)
                projectiles.add(projectile)
                projectileTimer = projectileDelay
            }
        }

        // Update projectiles
        val toBeDeleted: MutableList<Projectile> = mutableListOf()
        for (projectile in projectiles) {
            if (!projectile.update(dt)) {
                toBeDeleted.add(projectile)
            }
        }
        for (projectile in toBeDeleted) {
            projectiles.remove(projectile)
        }
    }
}