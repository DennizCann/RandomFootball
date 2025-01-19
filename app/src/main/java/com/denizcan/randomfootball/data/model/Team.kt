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
            childColumns = ["leagueId"]
        )
    ],
    indices = [Index("leagueId")]
)
data class Team(
    @PrimaryKey(autoGenerate = true)
    val teamId: Long = 0,
    val name: String,
    val leagueId: Long,
    val managerId: Long = 0,
    val primaryColor: String,
    val secondaryColor: String
)
