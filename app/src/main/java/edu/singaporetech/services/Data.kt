package edu.singaporetech.services

import androidx.room.Entity
import androidx.room.PrimaryKey


/*
* Annotated with the @Entity annotation to indicate that it's a Room database Entity.
* A Room database Entity is a simple data model that represents a table in the database.
* */

@Entity(tableName = "highscore_table")
data class HighscoreData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val score: Int,
    val aliveTime: Float
)

@Entity(tableName = "projectiles_table")
data class ProjectilesData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val positionX: Float,
    val positionY: Float,
    val projectileVelocity: Float,
    val projectileBoundary: Float,
    val projectileType: Int
)

@Entity(tableName = "enemy_table")
data class EnemyData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val positionX: Float,
    val positionY: Float,
    val velocityX: Float,
    val projectileDamage: Int,
    val projectileDelay: Float,
    val projectileTimer: Float,
    val projectileVelocity: Float,
    val isAutoShoot: Boolean,
    val powerUpTimer: Float
)

@Entity(tableName = "player_table")
data class PlayerData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val positionX: Float,
    val positionY: Float,
    val velocityX: Float,
    val score: Int,
    val health: Int,
    val projectileDamage: Int,
    val projectileSpeed: Float
)