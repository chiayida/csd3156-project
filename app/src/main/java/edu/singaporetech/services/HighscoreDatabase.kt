package edu.singaporetech.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/*
* Provides an interface for data stored in SQLite database in Android application.
* The interface defines methods for inserting, reading, and deleting data from table.
* */
@Database(entities = [Highscore::class], version = 1, exportSchema = false)
abstract class HighscoreDatabase : RoomDatabase() {

    abstract fun highscoreDao(): HighscoreDao

    companion object {

        @Volatile
        private var mInstance: HighscoreDatabase? = null

        // Static method to create a singleton instance
        fun getDatabase(context: Context): HighscoreDatabase {
            val tempInstance = mInstance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HighscoreDatabase::class.java,
                    "highscore_database"
                ).build()
                mInstance = instance
                return instance
            }
        }
    }
}