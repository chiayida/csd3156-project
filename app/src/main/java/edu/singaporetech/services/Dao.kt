package edu.singaporetech.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


/*
* A Data Access Object class which provides methods for performing operations
* on the database such as inserting, querying, and deleting data.
* */

@Dao
interface HighscoreDao {
    @Insert
    fun insert(highscore: HighscoreData)

    // Retrieve top 5 scores in table
    @Query("SELECT * FROM highscore_table ORDER BY score DESC LIMIT 5")
    fun getTopScores(): List<HighscoreData>

    // Delete data from table
    @Query("DELETE FROM highscore_table")
    fun deleteAllHighscore()
}

@Dao
interface ProjectilesDao {
    @Insert
    fun insert(projectiles: ProjectilesData)

    // Retrieve list of projectiles
    @Query("SELECT * FROM projectiles_table ORDER BY id ASC")
    fun getProjectilesData(): List<ProjectilesData>

    // Delete data from table
    @Query("DELETE FROM projectiles_table")
    fun deleteAllProjectiles()
}

@Dao
interface EnemyDao {
    @Insert
    fun insert(enemy: EnemyData)

    // Retrieve list of enemies
    @Query("SELECT * FROM enemy_table ORDER BY id ASC")
    fun getEnemiesData(): List<EnemyData>

    // Delete data from table
    @Query("DELETE FROM enemy_table")
    fun deleteAllEnemies()
}

@Dao
interface PlayerDao {
    @Insert
    fun insert(player: PlayerData)

    // Retrieve list of players
    @Query("SELECT * FROM player_table ORDER BY id ASC")
    fun getPlayersData(): List<PlayerData>

    // Delete data from table
    @Query("DELETE FROM player_table")
    fun deleteAllPlayers()
}