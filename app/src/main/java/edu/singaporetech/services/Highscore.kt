package edu.singaporetech.services

import androidx.room.Entity
import androidx.room.PrimaryKey


/*
* Annotated with the @Entity annotation to indicate that it's a Room database Entity.
* A Room database Entity is a simple data model that represents a table in the database.
* */
@Entity(tableName = "highscore_table")
data class Highscore (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val score: Int,
    val aliveTime: Float
)