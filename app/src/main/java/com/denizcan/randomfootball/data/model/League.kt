package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "leagues")
data class League(
    @PrimaryKey(autoGenerate = true) val leagueId: Long = 0, // Primary key
    val gameId: Long, // Foreign key
    val name: String
)
