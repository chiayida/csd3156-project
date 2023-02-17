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
    // Insert data to table
    @Insert
    fun insert(highscore: Highscore)

    // Retrieve top 5 scores in table
    @Query("SELECT * FROM highscore_table ORDER BY score DESC LIMIT 5")
    fun getTopScores(): List<Highscore>

    // Retrieve the aliveTime for top 5 scores in table
    @Query("SELECT aliveTime FROM highscore_table WHERE score IN (SELECT score FROM highscore_table ORDER BY score DESC LIMIT 5)")
    fun getTopAliveTimes(): List<Float>

    // Delete data from table
    @Query("DELETE FROM highscore_table")
    fun deleteAllHighscore()
}