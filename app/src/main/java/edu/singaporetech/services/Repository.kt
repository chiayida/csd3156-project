package edu.singaporetech.services

import android.content.Context
import androidx.room.Room


/*
* This repository provides a unified interface for the data stored in database
* */

class MyRepository(context: Context) {

    private val highscoreDao: HighscoreDao
    private val projectilesDao: ProjectilesDao
    private val enemyDao: EnemyDao
    //private val playerDao: PlayerDao

    init {
        val myDatabase = Room.databaseBuilder(context, MyDatabase::class.java, "my_database").build()
        highscoreDao = myDatabase.highscoreDao()
        projectilesDao = myDatabase.projectilesDao()
        enemyDao = myDatabase.enemyDao()
        //playerDao = myDatabase.playerDao()
    }


    // HighscoreData operations
    fun insertHighscoreData(highscoreData: HighscoreData) {
        highscoreDao.insert(highscoreData)
    }

    fun getTopHighscores(): List<HighscoreData> {
        return highscoreDao.getTopScores()
    }

    fun deleteAllHighscores() {
        highscoreDao.deleteAllHighscore()
    }


    // ProjectilesData operations
    fun insertProjectilesData(projectilesData: ProjectilesData) {
        projectilesDao.insert(projectilesData)
    }

    fun getProjectilesData(): List<ProjectilesData> {
        return projectilesDao.getProjectilesData()
    }

    fun deleteAllProjectiles() {
        projectilesDao.deleteAllProjectiles()
    }


    // EnemyData operations
    fun insertEnemyData(enemyData: EnemyData) {
        enemyDao.insert(enemyData)
    }

    fun getEnemiesData(): List<EnemyData> {
        return enemyDao.getEnemiesData()
    }

    fun deleteAllEnemies() {
        enemyDao.deleteAllEnemies()
    }


    /*
    // PlayerData operations
    fun insertPlayerData(playerData: PlayerData) {
        playerDao.insert(playerData)
    }

    fun getPlayersData(): List<PlayerData> {
        return playerDao.getPlayersData()
    }

    fun deleteAllPlayers() {
        playerDao.deleteAllPlayers()
    }
     */
}