package edu.singaporetech.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/*
* Provides an interface for data stored in SQLite database in Android application.
* The interface defines methods for inserting, reading, and deleting data from table.
* */

@Database(entities = [HighscoreData::class, ProjectilesData::class, EnemyData::class, PlayerData::class],
    version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    abstract fun highscoreDao(): HighscoreDao
    abstract fun projectilesDao(): ProjectilesDao
    abstract fun enemyDao(): EnemyDao
    abstract fun playerDao(): PlayerDao

    companion object {

        @Volatile
        private var mInstance: MyDatabase? = null

        // Static method to create a singleton instance
        fun getDatabase(context: Context): MyDatabase {
            val tempInstance = mInstance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "my_database"
                ).build()
                mInstance = instance
                return instance
            }
        }
    }
}