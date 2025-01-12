package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "teams",
    foreignKeys = [
        ForeignKey(
            entity = League::class,
            parentColumns = ["leagueId"],
            childColumns = ["leagueId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["leagueId"])]
)
data class Team(
    @PrimaryKey(autoGenerate = true) val teamId: Long = 0, // Primary key
    val leagueId: Long, // Foreign key
    val name: String,
    val primaryColor: String,
    val secondaryColor: String
)
