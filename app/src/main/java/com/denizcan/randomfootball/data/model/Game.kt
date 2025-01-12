package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val gameId: Long = 0,
    val name: String,
    val creationDate: Date,
    val selectedTeamId: Long? = null  // Seçilen takımın ID'si
) 