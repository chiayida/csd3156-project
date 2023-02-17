package edu.singaporetech.services

import android.content.Context


/*
* This repository provides a unified interface for the data stored in database
* */
class HighscoreRepository(context: Context) {

    private val highscoreDao: HighscoreDao

    init {
        val database = HighscoreDatabase.getDatabase(context)
        highscoreDao = database.highscoreDao()
    }

    fun insert(highscore: Highscore) {
        highscoreDao.insert(highscore)
    }

    fun getTopScores(): List<Highscore> {
        return highscoreDao.getTopScores()
    }

    fun getTopAliveTimes(): List<Float> {
        return highscoreDao.getTopAliveTimes()
    }

    fun deleteAllHighscore() {
        highscoreDao.deleteAllHighscore()
    }
}