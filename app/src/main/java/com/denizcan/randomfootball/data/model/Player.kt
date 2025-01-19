package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    foreignKeys = [
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamId"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Player(
    @PrimaryKey(autoGenerate = true) val playerId: Long = 0,
    val teamId: Long, // Foreign key
    val name: String,
    val shirtNumber: Int,
    val skill: Int,
    val position: String,
    val nationality: String
)